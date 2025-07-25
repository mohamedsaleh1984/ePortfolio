from dash import Dash, dcc, html, dash_table, ctx
import dash_leaflet as dl
from dash.dependencies import Input, Output, State
import plotly.express as px
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import mysql.connector
from mysql.connector import Error

from helper import Helper
from animalShelter import AnimalShelter

# MySQL Connection Parameters
db_config = {
    'host': 'localhost',
    'database': 'shelter',  # Your database name
    'user': 'root',
    'password': '5624566'
}

# instances
_helper = Helper()
_shelter = AnimalShelter(db_config.get("host"),
                        db_config.get("database"),
                        db_config.get("user"),
                        db_config.get("password"))

# App Initialization
app = Dash(__name__)
app.title = _helper.getAppName()
encoded_image = _helper.getImage()
link_url = _helper.getUrl()




# Run the app
if __name__ == '__main__':
    app.run_server(debug=True)