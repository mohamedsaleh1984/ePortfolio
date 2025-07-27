"""
@author: mohamedsaleh2_snhu
"""

# Add MySQL dependencies
import mysql.connector
from mysql.connector import Error

"""
AnimalShelter to handle users Database requests.
"""
class AnimalShelter(object):
    """ CRUD operatoins for animal collection in MySQL"""
    
    def __init__(self, db_config):
        """
        Construct Db Connection with MySQL Database
        """
        try:
            # init internal property for Animal Class
            self.connection = None
            # initiate MySQL connection
            self.connection = mysql.connector.connect(**db_config)
            # attached cursor object to MySQL connection
            self.cursor = self.connection.cursor(dictionary=True)
            print("Connection to MySQL DB successful")
        except Error as e:
            print(f"The error '{e}' occurred")
            raise

    
    def reading(self, sqlStatement):
        """
        Execute SQl Statement to fetch user's request.    
        """
        try:
            
            if sqlStatement == None or sqlStatement == "": 
                sqlStatement = f"SELECT * FROM Pets"
                    
            self.cursor.execute(sqlStatement)
            result = self.cursor.fetchall()
            return result
        except Error as e:
            print(f'Failed to execute find: {e}')
            return None
    
    def __del__(self):
        # Clean up connection when object is destroyed
        if hasattr(self, 'cursor'):
            self.cursor.close()
        
        if hasattr(self, 'connection') and self.connection.is_connected():
            self.connection.close()
