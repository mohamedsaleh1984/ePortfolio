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
from QueryHelper import QueryHelper

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
_queryHelper = QueryHelper()

# class read method must support return of list object and accept projection json input
# sending the read method an empty document requests all documents be returned
# handle unhandled exception
try:
    df = pd.DataFrame.from_records(_shelter.reading(""))
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
animal_type= _helper.animal_type()
animal_breed = _helper.animals_breeds()
column_name_mapping = _helper.getCoulmnMapping()
sel_rescue_type = []

# Store the original DataFrame globally
original_df = df.copy()

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
app.layout = html.Div(
    [
     html.Div( 
        style={'display' : 'flex'},
        children=[
                html.Div(id='logo-image-id',style={'width':'20%'}, children=[
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
                html.Div(id='app-title-id',style={'width':'80%'}, children=[
                    html.Center(html.B(html.P(_helper.getTitle(), 
                    style={'color':'#94190c',
                        'textAlign':'left',
                        'marginLeft':'50px',
                        'margin':'auto',
                        'fontSize':'24px',
                        'marginTop':'100px'}))),
                ])
        ]), 
    html.Hr(),
    html.Div(
        className='row',
        style={'display' : 'flex'},
        children=[
            dcc.Checklist(
                id="pet-category-list",
                options=['Water Rescue', 'Wilderness Rescue', 'Disaster'],
                value=[],
                inline=True,
                style = {
                            'color':'#94190c',
                            'fontFamily':'Open Sans, verdana, arial, sans-serif',
                            'fontWeight':'bold',
                        }
            ),
            
            html.Center('Type', style={'color':'#94190c',
                            'fontFamily':'Open Sans, verdana, arial, sans-serif',
                            'fontWeight':'bold',
                            'marginLeft':'5px'
                            }) ,
            
            dcc.Dropdown(animal_type, '----------', id='dropdown-animal-type',style={'width':"200px",'marginLeft':'5px'}),
            
            html.Center('Breed', style={'color':'#94190c',
                            'fontFamily':'Open Sans, verdana, arial, sans-serif',
                            'fontWeight':'bold',
                            'marginLeft':'10px'}) ,

            dcc.Dropdown(animal_breed, '----------', id='dropdown-animal-breed',style={'width':"200px",'marginLeft':'5px'}),

            html.Button('Search', id='button-search-id', n_clicks=0, style={'marginLeft':'25px','color':'#94190c', 'fontWeight':'bold','borderColor':'#94190c','borderRadius':'10px'}),

            html.Button('Reset', id='button-reset-id', n_clicks=0,style={'marginLeft':'5px','color':'#94190c', 'fontWeight':'bold','borderColor':'#94190c', 'borderRadius':'10px'}),

        ]),

    html.Hr(),
    
    dash_table.DataTable(
        id='datatable-id',
        columns=[{"name": column_name_mapping.get(i, i),  # Falls back to original name if not in mapping
             "id": i, 
             "deletable": False, 
             "selectable": True} for i in df.columns]
             ,data=df.to_dict('records'),
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
     html.Div(className='row',
        style={'display' : 'flex'},
        children=[
                html.Div(id='graph-id',className='col s12 m6'),
                html.Div(id='map-id',className='col s12 m6')
        ])
])



# Display the breeds of animal based on quantity represented in the data table
@app.callback(
    Output('graph-id', "children"),
    [Input('datatable-id', "derived_virtual_data")])
def update_graphs(viewData):
    dataset = pd.DataFrame(viewData)
    if not dataset.empty:
        return [dcc.Graph(figure = px.pie(dataset, names='breed',title='Preferred Animal Breeds',color_discrete_sequence=px.colors.sequential.RdBu))]
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
    
    # render map
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




@app.callback(
    Output('datatable-id', 'data'),
    Output('pet-category-list', 'value'),
    Output('dropdown-animal-type', 'value'),
    Output('dropdown-animal-breed', 'value'),
    Input('button-search-id', 'n_clicks'),
    Input('button-reset-id', 'n_clicks'),
    State('pet-category-list', 'value'),
    State('dropdown-animal-type', 'value'),
    State('dropdown-animal-breed', 'value'),
    prevent_initial_call=True
)
def update_table(search_clicks, reset_clicks, categories, animal_type, animal_breed):
    triggered = ctx.triggered_id

    if triggered == 'button-reset-id':
        # Return full dataset + reset component values
        df_all = pd.DataFrame.from_records(_shelter.reading(""))
        return df_all.to_dict('records'), [], '----------', '----------'

    elif triggered == 'button-search-id':
        # Build query filter based on selected values
        query = {}

        if categories:
            query["rescuer_category"] =  categories
        if animal_type != '----------':
            query["animal_type"] = animal_type
        if animal_breed != '----------':
            query["breed"] = animal_breed

        try:
            sql_statement = _queryHelper.Build(query)
            print('sql_statement ', sql_statement )
            df_filtered = pd.DataFrame.from_records(_shelter.reading(sql_statement))
            return df_filtered.to_dict('records'), categories, animal_type, animal_breed
        except Exception as e:
            print(f"Query failed: {e}")
            return [], categories, animal_type, animal_breed
        

# Run the app
if __name__ == '__main__':
    app.run_server(debug=True)