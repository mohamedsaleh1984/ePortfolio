class QueryHelper():
    def WaterRescue(self):
        wr_sql = """
                SELECT *
                FROM Pets
                WHERE animal_type = 'Dog'
                AND breed IN ('Labrador Retriever Mix', 'Chesapeake Bay Retriever', 'Newfoundland')
                AND sex_upon_outcome = 'Intact Female'
                AND age_upon_outcome_in_weeks BETWEEN 26 AND 156;
                """
        return wr_sql
    
    def WildernessRescue(self):
        wir_sql = """
            SELECT *
            FROM Pets
            WHERE animal_type = 'Dog'
            AND breed IN ('German Shepherd', 'Alaskan Malamute', 'Old English Sheepdog', 'Siberian Husky', 'Rottweiler')
            AND sex_upon_outcome = 'Intact Male'
            AND age_upon_outcome_in_weeks BETWEEN 26 AND 156;
            """
        return wir_sql

    def DisasterRescue(self):
        dr_sql = """
            SELECT *
            FROM Pets
            WHERE animal_type = 'Dog'
            AND breed IN ('Doberman Pinscher', 'German Shepherd', 'Golden Retriever', 'Bloodhound', 'Rottweiler')
            AND sex_upon_outcome = 'Intact Male'
            AND age_upon_outcome_in_weeks BETWEEN 20 AND 300;"""
        return dr_sql

    def Build(self, query):
        """
        Convert Dictionary to SQL Statement
        """
        sql_statement = ""

        if "rescuer_category" in query:
            for item in query['rescuer_category']:
                if len(sql_statement) > 0:
                    sql_statement+= "\nUnion\n"

                if item == 'Water Rescue':
                    sql_statement+= self.WaterRescue()
                elif item == 'Disaster':
                    sql_statement+= self.DisasterRescue()
                elif item == 'Wilderness Rescue':
                    sql_statement+= self.WildernessRescue()

        type = ""

        if len(sql_statement) > 0:
            sql_statement += "\nUnion\n"

        if "animal_type" in query and "breed" in query:
            type = "SELECT * FROM Pets Where animal_type = '"+ query['animal_type'] +"' AND breed='"+ query['breed']+"'"
        elif "animal_type" in query and "breed" not in query:
            type = "SELECT * FROM Pets Where animal_type = '"+ query['animal_type'] +"'"
        elif "animal_type" not in query and "breed" in query:
            type = "SELECT * FROM Pets Where breed = '"+ query['breed']+"'"
        
        sql_statement += type
        
        return sql_statement