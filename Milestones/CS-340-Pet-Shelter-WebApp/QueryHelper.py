class QueryHelper():
    def WaterRescue(self):
        wr_sql = """
                SELECT *
                FROM Pets
                WHERE animal_type = 'Dog'
                AND breed IN ('Labrador Retriever Mix', 'Chesapeake Bay Retriever', 'Newfoundland')
                AND sex_upon_outcome = 'Intact Female'
                AND age_upon_outcome_in_weeks BETWEEN 26 AND 156
                """
        return wr_sql
    
    def WildernessRescue(self):
        wir_sql = """
            SELECT *
            FROM Pets
            WHERE animal_type = 'Dog'
            AND breed IN ('German Shepherd', 'Alaskan Malamute', 'Old English Sheepdog', 'Siberian Husky', 'Rottweiler')
            AND sex_upon_outcome = 'Intact Male'
            AND age_upon_outcome_in_weeks BETWEEN 26 AND 156
            """
        return wir_sql

    def DisasterRescue(self):
        dr_sql = """
            SELECT *
            FROM Pets
            WHERE animal_type = 'Dog'
            AND breed IN ('Doberman Pinscher', 'German Shepherd', 'Golden Retriever', 'Bloodhound', 'Rottweiler')
            AND sex_upon_outcome = 'Intact Male'
            AND age_upon_outcome_in_weeks BETWEEN 20 AND 300"""
        return dr_sql

    def Build(self, query):
        """
        Convert Dictionary to SQL Statement
        """
        sql_statement = ""
        list_statements = []
        if "rescuer_category" in query:
            for item in query['rescuer_category']:
                if item == 'Water Rescue':
                    list_statements.append(self.WaterRescue())
                elif item == 'Disaster':
                    list_statements.append(self.DisasterRescue())
                elif item == 'Wilderness Rescue':
                    list_statements.append(self.WildernessRescue())

        
        if "animal_type" in query and "breed" in query:
            type = "SELECT * FROM Pets Where animal_type = '"+ query['animal_type'] +"' AND breed='"+ query['breed']+"'"
            list_statements.append(type)
        elif "animal_type" in query and "breed" not in query:
            type = "SELECT * FROM Pets Where animal_type = '"+ query['animal_type'] +"'"
            list_statements.append(type)
        elif "animal_type" not in query and "breed" in query:
            type = "SELECT * FROM Pets Where breed = '"+ query['breed']+"'"
            list_statements.append(type)

        # build sql statement with union.
        if len(list_statements) == 0:        
            sql_statement = ""
        elif len(list_statements) == 1:
            sql_statement = list_statements[0]
        elif len(list_statements) > 1:
            sql_statement = ""
            for index, item in enumerate(list_statements):
                new = "("+item+")"
                sql_statement += new
                if index + 1 != len(list_statements):
                    sql_statement += "\nUnion\n"                        
        return sql_statement
