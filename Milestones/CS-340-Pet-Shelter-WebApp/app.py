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
_shelter = AnimalShelter(db_config)

# class read method must support return of list object and accept projection json input
# sending the read method an empty document requests all documents be returned
# handle unhandled exception
try:
    df = pd.DataFrame.from_records(_shelter.reading({}))
    # if "_id" in df.columns:
    #     df.drop(columns=['_id'],inplace=True)
except Exception as e:
    df = pd.DataFrame()
    print(f"Failed to read data: {e}")


# App Initialization
app = Dash(__name__)
app.title = _helper.getAppName()
encoded_image = _helper.getImage()
link_url = _helper.getUrl()

app.layout = html.Div(
    [
     html.Div( style={'display' : 'flex'},
        children=[
                html.Div(id='graph-id',style={'width':'20%'}, children=[
                    html.A(
                        href = link_url,
                        children = [
                            html.Img(
                                src = 'data:image/png;base64,{}'.format(encoded_image.decode()),
                                alt = 'Company Logo',
                                width = '100%',
                                height = '100%',
                                style = {'border': '1px dashed #OE4D92'})
                        ]),
                ]),
                html.Div(id='map-id',style={'width':'80%'}, children=[
                    html.Center(html.B(html.P('Grazioso Salvare Dashboard By Mohamed Saleh', 
                    style={'color':'#94190c',
                        'textAlign':'left',
                        'marginLeft':'50px',
                        'margin':'auto',
                        'marginTop':'100px'}))),
                ])
        ]), 
    html.Hr(),
    html.Div(
        className='row',
        style={'display' : 'flex'},
        children=[
            dcc.RadioItems(
                id='rd-selection',
                options=[
                    {'label': 'Water Rescue', 'value': 'wr'},
                    {'label': 'Wilderness Rescue', 'value': 'wir'},
                    {'label': 'Disaster', 'value': 'dr'},
                    {'label': 'Reset', 'value': 'reset'}
                ],
                value='reset',
                inline=True,
                style = {
                            'color':'#94190c',
                            'fontFamily':'Open Sans, verdana, arial, sans-serif',
                            'fontWeight':'bold'
                        }
            )
        ]),
    html.Hr(),
    html.Div(id='debug-id'),
    dash_table.DataTable(
        id='datatable-id',columns=[{"name": i, "id": i, "deletable": False, "selectable": True} for i in df.columns],data=df.to_dict('records'),
        editable=False, # allow Edit 
        filter_action="native",sort_action="native", 
        sort_mode='multi', # sort using different parameter
        row_selectable='single', # Select ONE row at a time
        row_deletable=False, # Don't allow delete
        selected_rows=[0], # set the default selection for row index
        page_action="native", 
        page_current=0, # current page selection
        page_size=10 # number of rows per page
        ),
    html.Br(),
    html.Hr(),
    # This sets up the dashboard so that your chart and your geolocation chart are side-by-side
    #  html.Div(className='row',
    #     style={'display' : 'flex'},
    #     children=[
    #             html.Div(id='graph-id',className='col s12 m6'),
    #             html.Div(id='map-id',className='col s12 m6')
    #     ])
])


# Display the breeds of animal based on quantity represented in the data table
@app.callback(
    Output('graph-id', "children"),
    [Input('datatable-id', "derived_virtual_data")])
def update_graphs(viewData):
    dataset = pd.DataFrame(viewData)
    if not dataset.empty:
        return [dcc.Graph(
            figure = px.pie(dataset, 
                            names='breed',
                            title='Preferred Animal Breeds',
                            color_discrete_sequence=px.colors.sequential.RdBu
                           ))]
    return html.Div("No data avaliable.")
    
# This callback will update the geo-location chart for the selected data entry
@app.callback(
    Output('map-id', "children"),
    [Input('datatable-id', "derived_virtual_data"),
     Input('datatable-id', "derived_virtual_selected_rows")])
def update_map(viewData, index):  
    dff = pd.DataFrame.from_dict(viewData)
    # Because we only allow single row selection, the list can 
    # be converted to a row index here
    if index is None:
        return [html.H3("loading...")]
    else:
        row = index[0]
    
    rowData = dff.iloc[row]
    
    # Animal Properties
    animalName = "name : " + _helper.check_and_replace(rowData['name'],"No Assigned Name")
    animalBreed = _helper.check_and_replace(rowData['breed'],"No Assigned Breed")
    animalType = "type : " + _helper.check_and_replace(rowData['animal_type'],"No Type Assigned")
    animalColor = "color : " + _helper.check_and_replace(rowData['color'],"N/A")
    # Animal geolocation
    lat, long = rowData['location_lat'], rowData['location_long']
    if pd.isna(lat) or pd.isna(long):
        return [html.H3("No location data available for this animal")]
    map_center = [lat,long]
    
    # render 
    return [
        dl.Map(style={'width': '1000px', 'height': '500px'},
           center= map_center, zoom=10, children=[
           dl.TileLayer(id="base-layer-id"),
           dl.Marker(
                position = map_center,
               children=[
                      dl.Tooltip(animalBreed),
                      dl.Popup(
                        [
                             html.H3(animalName),
                             html.H3(animalType),
                             html.H3(animalColor)
                        ])
                  ]
            ),
       ]),
    ]


#This callback will highlight a row on the data table when the user selects it
@app.callback(
    Output('datatable-id', 'style_data_conditional'), 
    [Input('datatable-id', 'selected_rows')]
)
def update_styles(selected_rows):
    if selected_rows:
        return [{
            'if': {'row_index': row}, 
            'backgroundColor': '#D2F3FF' 
        } for row in selected_rows] 
    return []


##############################################
# Process User Filters
##############################################
@app.callback(
    Output('datatable-id', "data"),
    Input('rd-selection', 'value'),
)
def process_filter(selection):
    # filter object
    filterObj = {}
    #Water Rescue
    if selection == "wr":
        desiredBreeds = ["Labrador Retriever Mix","Chesapeake Bay Retriever","Newfoundland"]
        filterObj["animal_type"] = "Dog"
        filterObj["breed"] = {"$in":desiredBreeds}
        filterObj["sex_upon_outcome"] = "Intact Female"
        filterObj["$and"]=[
                            {"age_upon_outcome_in_weeks":{"$gte":26}},
                            {"age_upon_outcome_in_weeks":{"$lte":156}}
                          ]
        
    # Wilderness Rescue
    if selection == "wir":
        desiredBreeds = ["German Shepherd", "Alaskan Malamute","Old English Sheepdog", "Siberian Husky","Rottweiler"]
        filterObj["animal_type"] = "Dog"
        filterObj["breed"] = {"$in":desiredBreeds}
        filterObj["sex_upon_outcome"] = "Intact Male"
        filterObj["$and"]=[
                            {"age_upon_outcome_in_weeks":{"$gte":26}},
                            {"age_upon_outcome_in_weeks":{"$lte":156}}
                          ]
        
    # Disaster Rescue
    if selection == "dr":
        desiredBreeds = ["Doberman Pinscher", "German Shepherd", "Golden Retriever","Bloodhound", "Rottweiler"]
        filterObj["animal_type"] = "Dog"
        filterObj["breed"] = {"$in":desiredBreeds}
        filterObj["sex_upon_outcome"] = "Intact Male"
        filterObj["$and"]=[
                            {"age_upon_outcome_in_weeks":{"$gte":20}},
                            {"age_upon_outcome_in_weeks":{"$lte":300}}
                          ]
        
    # fetch data with filter object
    data = _shelter.reading(filterObj)
    filteredDataFrame = pd.DataFrame.from_records(data)
    # remove _id
    if "_id" in filteredDataFrame.columns:
        filteredDataFrame.drop(columns=['_id'], inplace=True)
        
    return filteredDataFrame.to_dict('records')



# Run the app
if __name__ == '__main__':
    app.run_server(debug=True)