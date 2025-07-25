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

# Import CRUD module
from animalShelter import AnimalShelter

# Connect to MongoDB
username = "aacuser"
password = "5624566"
shelter = AnimalShelter(username, password)

# Initial data load
try:
    df = pd.DataFrame.from_records(shelter.reading({}))
    if "_id" in df.columns:
        df.drop(columns=['_id'], inplace=True)
except Exception as e:
    df = pd.DataFrame()
    print(f"Failed to read data: {e}")

# Helper
def check_and_replace(string, message):
    if string is None or len(string) == 0:
        return message
    return string

# App Initialization
app = Dash(__name__)
app.title = "Grazioso Salvare Dashboard"

# Encode logo image
image_filename = 'Grazioso_Salvare_Logo.png'
if not os.path.exists(image_filename):
    print(f"WARNING: Logo file '{image_filename}' not found.")
    encoded_image = b""
else:
    encoded_image = base64.b64encode(open(image_filename, 'rb').read())

link_url = "https://www.snhu.edu/"

# App Layout
app.layout = html.Div([...])  # You can keep the full layout unchanged from your original code

# The rest of the callbacks remain unchanged.
# Replace JupyterDash with Dash, and ensure image file and animalShelter module are accessible.

# Run the app
if __name__ == '__main__':
    app.run_server(debug=True)
