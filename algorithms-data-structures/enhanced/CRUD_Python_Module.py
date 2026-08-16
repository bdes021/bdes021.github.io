# Example Python Code to Insert a Document 

import pymongo
from pymongo import MongoClient 
from bson.objectid import ObjectId 

class AnimalShelter(object): 
    def __init__(self, user, password): 
        # Initializing the MongoClient. This helps to access the MongoDB 
        # databases and collections. This is hard-wired to use the aac 
        # database, the animals collection, and the aac user. 
        # 
        self.client = None
        self.database = None
        # You must edit the password below for your environment. 
        # 
        # Connection Variables 
        # 
        USER = 'aacuser' 
        PASS = 'cs-340' 
        HOST = 'localhost' 
        PORT = 27017 
        DB = 'aac' 
        COL = 'animals' 
        # 
        # Initialize Connection 
        # 
        try:
            # The URI includes the authentication database ('admin') as part of the connection string
            uri = f"mongodb://{user}:{password}@{HOST}:{PORT}/?authSource=admin"
            self.client = MongoClient(uri)
            
            # Ping the server to check connection and raise an exception if authentication fails
            self.client.admin.command('ping')
            print("INFO: Successfully connected to MongoDB.")
            
            # Set the database object
            self.database = self.client[DB]
            # Set the collection object for direct access
            self.collection = self.database[COL] 
            
        except pymongo.errors.ConnectionFailure as e:
            print(f"ERROR: Could not connect to MongoDB: {e}")
            self.client = None
        except pymongo.errors.OperationFailure as e:
            print(f"ERROR: Authentication failed for user {user}: {e}")
            self.client = None
        except Exception as e:
            print(f"ERROR: An unexpected error occurred during connection: {e}")
            self.client = None

    # Create a method to return the next available record number for use in the create method
            
    # Complete this create method to implement the C in CRUD. 
    def create(self, data: dict) -> bool:
        # Ensure connection and data integrity before proceeding
        if self.collection is None:
            print("ERROR: Connection not established, cannot create document.")
            return False
        if not data or not isinstance(data, dict):
            print("ERROR: Nothing to save or data parameter is not a dictionary.")
            return False

        try:
            # Insert the document
            result = self.collection.insert_one(data)
            
            # Check for successful insertion acknowledgement
            if result.acknowledged:
                print(f"SUCCESS: Document inserted with ID: {result.inserted_id}")
                return True
            else:
                print("FAILURE: Insertion was not acknowledged.")
                return False
                
        except Exception as e:
            print(f"ERROR: An unexpected error occurred during document creation: {e}")
            return False

    # Create method to implement the R in CRUD.
    def read(self, query, projection=None, sort=None, limit=0):
        """
        Reads documents from the MongoDB collection.

        Parameters:
            query (dict): MongoDB query filter.
            projection (dict, optional): Fields to include or exclude.
            sort (list, optional): List of tuples specifying sort order.
            limit (int, optional): Maximum number of documents to return.

        Returns:
            list: Matching documents.
        """
        try:
            cursor = self.collection.find(query, projection)

            if sort:
                cursor = cursor.sort(sort)

            if limit > 0:
                cursor = cursor.limit(limit)

            results = list(cursor)

            print(f"SUCCESS: Found {len(results)} document(s) matching the query.")
            return results

        except Exception as e:
            print(f"ERROR: Failed to read documents. {e}")
            return []

    def build_animal_hashmap(self, animals):
        """
        Stores animal records in a dictionary keyed by animal_id.
        Because animal_id values may appear more than once, each key maps
        to a list of matching records.

        Parameters:
            animals (list): Animal documents returned from MongoDB.

        Returns:
            dict: HashMap of animal records keyed by animal_id.
        """
        animal_map = {}

        for animal in animals:
            animal_id = animal.get("animal_id")

            if not animal_id:
                continue

            if animal_id not in animal_map:
                animal_map[animal_id] = []

            animal_map[animal_id].append(animal)

        return animal_map

    def find_by_animal_id(self, animal_map, animal_id):
        """
        Retrieves records from the HashMap using an animal ID.
        Average lookup time is O(1).

        Parameters:
            animal_map (dict): HashMap created by build_animal_hashmap().
            animal_id (str): Animal ID to locate.

        Returns:
            list: Records matching the animal ID.
        """
        return animal_map.get(animal_id, [])

    def sort_animals(self, animals, sort_field, descending=False):
        """
        Sorts animal records by the selected field using Python's built-in
        Timsort algorithm.

        Parameters:
            animals (list): Animal records to sort.
            sort_field (str): Field used for sorting.
            descending (bool): Sorts in descending order when True.

        Returns:
            list: A new sorted list of animal records.
        """
        try:
            return sorted(
                animals,
                key=lambda animal: str(
                    animal.get(sort_field, "")
                ).lower(),
                reverse=descending
            )

        except Exception as error:
            print(f"ERROR: Failed to sort animal records. {error}")
            return animals
    
    def update(self, query: dict, update_data: dict) -> int:
        if self.collection is None or not isinstance(query, dict) or not isinstance(update_data, dict):
            print("ERROR: Invalid input for update or connection issue.")
            return 0
        # MongoDB best practice is to require an update operator
        if not any(key.startswith('$') for key in update_data.keys()):
            print("WARNING: Update data missing MongoDB operator (e.g., $set). Automatically adding $set.")
            update_data = {"$set": update_data}

        try:
            # Use update_many to update all documents matching the query
            result = self.collection.update_many(query, update_data)
            
            print(f"SUCCESS: Matched {result.matched_count} documents. Modified {result.modified_count} documents.")
            return result.modified_count
            
        except Exception as e:
            print(f"ERROR: Document update failed: {e}")
            return 0
        
    
    def delete(self, query: dict) -> int:
        if self.collection is None or not isinstance(query, dict) or not query:
            # Prevents accidental deletion of entire collection (empty query)
            print("ERROR: Invalid query (cannot be empty) or connection issue.")
            return 0
        
        try:
            # Use delete_many to delete all documents matching the query
            result = self.collection.delete_many(query)
            
            print(f"SUCCESS: Deleted {result.deleted_count} documents.")
            return result.deleted_count
            
        except Exception as e:
            print(f"ERROR: Document deletion failed: {e}")
            return 0



## TEMPORARY TESTING AREA
if __name__ == "__main__":
    shelter = AnimalShelter("aacuser", "cs-340")

    if shelter.client is not None:
        animals = shelter.read(
            {"animal_type": "Dog"},
            projection={
                "_id": 0,
                "animal_id": 1,
                "name": 1,
                "animal_type": 1,
                "breed": 1,
                "outcome_type": 1
            },
            limit=20
        )

        sorted_animals = shelter.sort_animals(
            animals,
            sort_field="breed"
        )

        print("\nDOGS SORTED BY BREED")
        print("-" * 70)

        for animal in sorted_animals:
            print(
                f"{animal.get('animal_id')} | "
                f"{animal.get('name') or 'No name'} | "
                f"{animal.get('breed')} | "
                f"{animal.get('outcome_type')}"
            )