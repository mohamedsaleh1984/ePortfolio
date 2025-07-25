# Standard Imports
from dash import Dash, dcc, html, dash_table, ctx
import dash_leaflet as dl
from dash.dependencies import Input, Output, State
import plotly.express as px
import base64
import os
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import mysql.connector
from mysql.connector import Error

# MySQL Connection Parameters
db_config = {
    'host': 'localhost',
    'database': 'shelter',  # Your database name
    'user': 'root',
    'password': '5624566'
}

# Connect to MySQL
def create_connection():
    connection = None
    try:
        connection = mysql.connector.connect(**db_config)
        print("Connection to MySQL DB successful")
    except Error as e:
        print(f"The error '{e}' occurred")
    return connection

# Initial data load
try:
    connection = create_connection()
    if connection.is_connected():
        query = "SELECT * FROM Pets"  # Assuming your table is named 'animals'
        df = pd.read_sql(query, connection)
        # Drop any unwanted columns similar to the original code
        if "id" in df.columns:
            df.drop(columns=['id'], inplace=True)
except Exception as e:
    df = pd.DataFrame()
    print(f"Failed to read data: {e}")
finally:
    if connection and connection.is_connected():
        connection.close()

# Helper function remains the same
def check_and_replace(string, message):
    if string is None or len(string) == 0:
        return message
    return string

# App Initialization
app = Dash(__name__)
app.title = "Grazioso Salvare Dashboard"

# Encode logo image (unchanged)
image_filename = 'Grazioso_Salvare_Logo.png'
encoded_image = ""

if not os.path.exists(image_filename):
    print(f"WARNING: Logo file '{image_filename}' not found.")
    encoded_image = b""
else:
    encoded_image = base64.b64encode(open(image_filename, 'rb').read())

link_url = "https://www.snhu.edu/"

# Run the app
if __name__ == '__main__':
    app.run_server(debug=True)