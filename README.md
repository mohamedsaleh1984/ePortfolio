# Pet Shelter

#### Import CSV file to Database

1.  Install MySQL Workbench
2.  Create New Schema pet_shelter
3.  Right Click on `pet_shelter` and select Table Data Import Wizard from Navigator tab
4.  Select `aac_shelter_outcomes.csv` and leave table name as it is `aac_shelter_outcomes`
5.  Click Next and wait until the rows are imported.
6.  Click Next then finish

![alt text](./screenshot/import.png)

#### Install Dependencies

```sh
./install.bat
```

#### Change Database Connection

Update user, password according to the MySQL Server.

```py
db_config = {
    'host': 'localhost',
    'database': 'shelter',
    'user': 'root',
    'password': '5624566'
}
```

### Run the App

```sh
./run.bat
```

##### Database Export

Update `export.bat` file with desired location to export the database.
run `export.bat`

`C:\ProgramData\MySQL\MySQL Server 8.0\Data`

```
./export.bat
```

### Screenshot

Default Dataset
![alt text](./screenshot/screenshot-01.png)

Search for specific breed
![alt text](./screenshot/screenshot-02.png)

Search with different criteria
![alt text](./screenshot/screenshot-01.png)

# Inventory Mobile App

#### It's a simple Android application to manage inventory. The earlier version for the app was depedning on MySQL database, which not realistic nor practical. Though, form practice prespective it was enough to maniupver around UI elements, Workflow, Database Interactions, Permissions..ect.

#### The current version is an enhanced version, where Firebase Auth was added and real-time Firebase storgae incoperated to better and scalable soltiuon. The transition from old MySQL database to Firebase wasn't a straight forward. I conducted multiple refactoring for the project to encoperate Firebase Auth, Storage.

## Firebase Auth

![Firebase Auth Image](./screenshot/firebase-auth.png)

## Firebae Storage-database

![Firebase Storage Image](./screenshot/firebase-database.png)

## User Registeration

![User Registeration Image](./screenshot/register-user.png)

## Login

![User Login Image](./screenshot/login-user.png)

### Add/Update Item

![Update Item](./screenshot/update-item.png)

## View Items

### Showing items on Pixel 8

![View Item](./screenshot/items-pixel-8.png)

### Live update on Pixel 9 to simulate real-time update on two different devices.

![View Item](./screenshot/items-pixel-9.png)

## Edit Item

![Edit Item](./screenshot/Edit.png)

## Delete Item

![Delete Item](./screenshot/delete-item.png)

For more information please contact

Software Engineer: [Mohamed Saleh](mailto:mohamedsaleh1984@hotmail.com)
