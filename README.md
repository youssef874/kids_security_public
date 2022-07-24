
# Android application for kids security

This is android application developped with android framwork in kotlin an spring boot for backend.To allow parent track their kids. 


## Acknowledgements

 - [android developper](https://developer.android.com/)
 - [stack over flow](https://stackoverflow.com/)
 - [Baelding](https://www.baeldung.com/)
 - [meduim](https://medium.com/)


## API Reference

#### Get all kids

```http
  GET /children
```


#### Get child

```http
  GET /children/{childId}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `childId` | `long`   | **Required**. Id of child to fetch|


#### Get authenticated child

```http
  GET /children/name?name=?&psw=?
```

| Parameter | Type     | Description                              |
| :-------- | :------- | :--------------------------------        |
| `name`    | `String` | **Required**.name of child to fetch      |
| `psw`     | `String` | **Required**.password of child to fetch  |


#### Get child profile picture

```http
  GET /children/image/${childId}
```

| Parameter | Type     | Description                                     |
| :-------- | :------- | :--------------------------------               |
| `childId` | `long`   | **Required**. Id of child to fetch it is picture|

#### Post child

```http
  Post /children/{parentId}
```

| Parameter | Type     | Description                              |
| :-------- | :------- | :--------------------------------        |
| `parentId`| `long`   | **Required**. Id of parent child to fetch|
| `child`   | `Child`  | **Required**.child data to be added      |
| `image`   | `File`   | **Required**.child profile picture       |


#### Get child location

```http
  GET /locations/children/{childId}
```

| Parameter | Type     | Description                                       |
| :-------- | :------- | :--------------------------------                 |
| `childId` | `long`   | **Required**. Id of child to fetch it is locations|


#### Post location

```http
  Post /locations?lat=?&long=?idChild=?
```

| Parameter | Type     | Description                                           |
| :-------- | :------- | :--------------------------------                     |
| `lat`     | `long`   | **Required**. latitude for child location to add      |
| `long`    | `long`   | **Required**.longtiutude for the child location to add|


#### Get conversation message

```http
  GET /messages?childId=?&parentId=?
```

| Parameter | Type     | Description                                                                       |
| :-------- | :------- | :--------------------------------                                                 |
| `childId` | `long`   | **Required**. Id of child as one of the conversation parties to fetch the messages|
| `parentId`| `long`   | **Required**.Id of parent as one of the conversation parties to fetch the messages|


#### Post message

```http
  Post /messages?content=?&sender=?idParent=?idChild=?
```

| Parameter | Type     | Description                                                               |
| :-------- | :------- | :--------------------------------                                         |
| `content` | `String` | **Required**. Content of the message to add                               |
| `sender`  | `String` | **Required**. To specify the sender of the message to add(parent or child)|
| `idParent`| `long`   | **Required**. Id of parent one of the message parties to add              |
| `idChild` | `long`   | **Required**. Id of child one of the message parties to add               |

## Demo

In the screnshot file you'll find the demo for this app.


## Installation

To Install the project

First you will need to add in the code/Andoid/KidsSecurity/app/src/main/AndroidManifest.xml

`API_KEY` from google play console.

  Then you need to [get your api api key](https://developers.google.com/maps/documentation/android-sdk/cloud-setup)
 
 Add your computer ip address in code/Andoid/KidsSecurity/app/src/main/java/com/example/KidsSecurity/model/retrofit/RetrofitDaoImpl.kt
 
`ipv4 addrss`

Then [Download](https://dev.mysql.com/downloads/installer/) mysql.

Install the server and cofigurate your computer to be a [server](https://www.microfocus.com/documentation/idol/IDOL_12_0/MediaServer/Guides/html/English/Content/Getting_Started/Configure/_TRN_Set_up_MySQL.htm)

In code/spring boot/SecurityKidsBackend/src/main/recsources/application.properties

Enter your `Database name`,your server `user name` and your server `password`

Then run SecurityKidsBackend in IDE (intellij recommended) and run KidsSecurity of android folder in IDE too (Andoid Studio recommended)
to run the application since the app still in developpement.
    
