class QueryHelper():
    def WaterRescue(self):
        wr_sql = """
                (SELECT *
                FROM Pets
                WHERE animal_type = 'Dog'
                AND breed IN ('Labrador Retriever Mix', 'Chesapeake Bay Retriever', 'Newfoundland')
                AND sex_upon_outcome = 'Intact Female'
                AND age_upon_outcome_in_weeks BETWEEN 26 AND 156)
                """
        return wr_sql
    
    def WildernessRescue(self):
        wir_sql = """
            (SELECT *
            FROM Pets
            WHERE animal_type = 'Dog'
            AND breed IN ('German Shepherd', 'Alaskan Malamute', 'Old English Sheepdog', 'Siberian Husky', 'Rottweiler')
            AND sex_upon_outcome = 'Intact Male'
            AND age_upon_outcome_in_weeks BETWEEN 26 AND 156)
            """
        return wir_sql

    def DisasterRescue(self):
        dr_sql = """
            (SELECT *
            FROM Pets
            WHERE animal_type = 'Dog'
            AND breed IN ('Doberman Pinscher', 'German Shepherd', 'Golden Retriever', 'Bloodhound', 'Rottweiler')
            AND sex_upon_outcome = 'Intact Male'
            AND age_upon_outcome_in_weeks BETWEEN 20 AND 300)"""
        return dr_sql

    def Build(self, query):
        sql_statment = ""

        if "rescuer_category" in query:
            for item in query['rescuer_category']:
                if len(sql_statment) > 0:
                    sql_statment+= "\nUnion\n"

                if item == 'Water Rescue':
                    sql_statment+= self.WaterRescue()
                elif item == 'Disaster':
                    sql_statment+= self.DisasterRescue()
                elif item == 'Wilderness Rescue':
                    sql_statment+= self.WildernessRescue()

        type = ""
        if "animal_type" in query and "breed" in query:
            type = "(SELECT * FROM Pets Where animal_type = '"+ query['animal_type'] +"' AND breed='"+ query['breed']+"')"
        elif "animal_type" in query and "breed" not in query:
            type = "(SELECT * FROM Pets Where animal_type = '"+ query['animal_type'] +"')"
        elif "animal_type" not in query and "breed" in query:
            type = "(SELECT * FROM Pets Where breed = '"+ query['breed']+"')"
        
        
        if len(sql_statment) > 0 and len(type) > 0:
            sql_statment+= "\nUnion\n"
            sql_statment += type
        else:
            sql_statment += type

        return sql_statment