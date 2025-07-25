"""
@author: mohamedsaleh2_snhu
"""
from pymongo import MongoClient
from bson.objectid import ObjectId


class AnimalShelter(object):
    """ CRUD operatoins for animal collection in MongoDB"""
    def __init__(self, username,password):
        # Initizalizing the MongoClient.
        HOST = 'nv-desktop-services.apporto.com'
        PORT = 34281
        DB = 'AAC'
        COL = 'animals'
        # Initizalize connection
        try:    
            self.client = MongoClient('mongodb://%s:%s@%s:%d' % (username,password,HOST,PORT))
            self.database = self.client['%s' % (DB)]
            self.collection = self.database['%s' % (COL)]
            print("Connection Established..")
        except:
            print('Failed to establish connection with Mongodb')
        
    """Create new animal""" 
    def create(self, data):
        try:    
            """ Check give input data"""
            if type(data) is dict and data is not None:
                self.database.animals.insert_one(data)
                return True
            elif type(data) is list and data is not None:
                self.database.animals.insert_many(data)
                return True
            else:
                print('Invalid document Structure')
                return False
        except:
            print('Failed to create new document(s)')
        
    """Find animal based on key/value parameter object"""
    def reading(self, query):
        try:
            """ Check give input query datatype"""
            if query is None or type(query) is not dict:
                print('Invalid Query')
                return None
            else:
                res =  list(self.database.animals.find(query))
                return res;
        except:
            print('Failed to execute find')
                   
    """Update animal based on filterObj parameter"""
    def update(self, filterObj, updateObj):
        try:
            """ Check give input filterObj datatype"""
            if ((filterObj is None or type(filterObj) is not dict) or (updateObj is None or type(updateObj) is not dict)):
                print('Invalid filterObj Or updateObj')
                return -1
            else:
                result = self.database.animals.update_many(filterObj, updateObj,upsert=False)
                return result.matched_count
        except:
            print('Failed to execute update')
 
    
    """Delete animal based on filterObj parameter"""
    def delete(self, filterObj):
        try:
            """ Check give input filterObj datatype"""
            if filterObj is None or type(filterObj) is not dict:
                print('Invalid Delete Query')
                return None
            else:
                result = self.database.animals.delete_many(filterObj)
                return result.deleted_count
        except:
            print('Failed to execute delete')



