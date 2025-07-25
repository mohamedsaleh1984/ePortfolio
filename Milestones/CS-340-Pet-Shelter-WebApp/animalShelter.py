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

    def get_connection(self):
        return self.connection
          

    """Create new animal"""
    def create(self, data):
        try:
            if data is None:
                print('No data provided')
                return False
        
            if type(data) is dict:
                columns = ', '.join(data.keys())
                placeholders = ', '.join(['%s'] * len(data))
                sql = f"INSERT INTO Pets ({columns}) VALUES ({placeholders})"
                self.cursor.execute(sql, list(data.values()))
                self.connection.commit()
                return True
                
            elif type(data) is list:
                if len(data) == 0:
                    return False
                # Assume all dicts in list have same keys
                columns = ', '.join(data[0].keys())
                placeholders = ', '.join(['%s'] * len(data[0]))
                sql = f"INSERT INTO Pets ({columns}) VALUES ({placeholders})"
                self.cursor.executemany(sql, [list(item.values()) for item in data])
                self.connection.commit()
                return True
            else:
                print('Invalid document structure')
                return False
        except Error as e:
            print(f'Failed to create new record(s): {e}')
            self.connection.rollback()
            return False
    
    """Find animal based on key/value parameter object"""
    def reading(self, query):
        try:
            if query is None or type(query) is not dict:
                print('Invalid Query')
                return None
            
            sql = f"SELECT * FROM Pets"
            
            if query.keys().__len__() > 0: 
                where_clause = ' AND '.join([f"{key} = %s" for key in query.keys()])
                sql = f"SELECT * FROM Pets WHERE {where_clause}"

            print("***SQL***", sql)

            self.cursor.execute(sql, list(query.values()))
            result = self.cursor.fetchall()
            return result
        except Error as e:
            print(f'Failed to execute find: {e}')
            return None
    
    """Update animal based on filterObj parameter"""
    def update(self, filterObj, updateObj):
        try:
            if ((filterObj is None or type(filterObj) is not dict) or 
                (updateObj is None or type(updateObj) is not dict)):
                print('Invalid filterObj Or updateObj')
                return -1
                
            set_clause = ', '.join([f"{key} = %s" for key in updateObj.keys()])
            where_clause = ' AND '.join([f"{key} = %s" for key in filterObj.keys()])
            
            sql = f"UPDATE Pets SET {set_clause} WHERE {where_clause}"
            params = list(updateObj.values()) + list(filterObj.values())
            
            self.cursor.execute(sql, params)
            self.connection.commit()
            return self.cursor.rowcount
        except Error as e:
            print(f'Failed to execute update: {e}')
            self.connection.rollback()
            return -1
    
    """Delete animal based on filterObj parameter"""
    def delete(self, filterObj):
        try:
            if filterObj is None or type(filterObj) is not dict:
                print('Invalid Delete Query')
                return None
                
            where_clause = ' AND '.join([f"{key} = %s" for key in filterObj.keys()])
            sql = f"DELETE FROM Pets WHERE {where_clause}"
            
            self.cursor.execute(sql, list(filterObj.values()))
            self.connection.commit()
            return self.cursor.rowcount
        except Error as e:
            print(f'Failed to execute delete: {e}')
            self.connection.rollback()
            return None
    
    def __del__(self):
        # Clean up connection when object is destroyed
        if hasattr(self, 'cursor'):
            self.cursor.close()
        
        if hasattr(self, 'connection') and self.connection.is_connected():
            self.connection.close()
