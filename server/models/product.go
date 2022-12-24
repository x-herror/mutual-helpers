package models

import (
	"database/sql"
	"fmt"
	_ "github.com/go-sql-driver/mysql"
)

var db *sql.DB

type Item struct {
	Id           int    `json:"id"`
	Name         string `json:"name"`
	Category     string `json:"category"`
	Location     string `json:"location"`
	Time         string `json:"time"`
	ImageName    string `json:"imageName"`
	ImageWidth   int    `json:"imageWidth"`
	ImageHeight  int    `json:"imageHeight"`
	Phone        string `json:"phone"`
	OwnerAccount string `json:"ownerAccount"`
	Attributes   string `json:"attributes"`
	Description  string `json:"description"`
	Comments     string `json:"comments"`
}

type Test struct {
	Id   int    `json:"id"`
	Name string `json:"name"`
}

/*
CREATE TABLE items(
    `id` int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(255) NOT NULL,
    `category` VARCHAR(255) NOT NULL,
    `location` VARCHAR(255) NOT NULL,
    `time` VARCHAR(255) NOT NULL,
    `imageName` VARCHAR(255) NOT NULL,
    `imageWidth` INT NOT NULL,
    `imageHeight` INT NOT NULL,
    `phone` VARCHAR(255) NOT NULL,
    `ownerAccount` VARCHAR(255) NOT NULL,
    `attributes` VARCHAR(255) NOT NULL,
    `description` VARCHAR(255) NOT NULL,
	`comments` VARCHAR(255) NOT NULL
) ;
*/
func init() {
	dbuser := "root"
	dbpass := "200430"
	dbname := "mutualhelpers"
	var err error
	db, err = sql.Open("mysql", dbuser+":"+dbpass+"@tcp(127.0.0.1:3306)/"+dbname)
	if err != nil {
		fmt.Println("Err", err.Error())
	}
}

func GetItems() []Item {
	results, err := db.Query("SELECT * FROM items")
	if err != nil {
		fmt.Println("Err", err.Error())
		return nil
	}

	var items []Item
	for results.Next() {
		var prod Item
		// for each row, scan into the Product struct
		err = results.Scan(&prod.Id, &prod.Name, &prod.Category, &prod.Location, &prod.Time, &prod.ImageName, &prod.ImageWidth, &prod.ImageHeight, &prod.Phone, &prod.OwnerAccount, &prod.Attributes, &prod.Description, &prod.Comments)
		if err != nil {
			panic(any(err.Error())) // proper error handling instead of panic in your app
		}
		// append the product into products array
		items = append(items, prod)
	}

	return items

}

func GetItem(category string) *Item {
	prod := &Item{}

	results, err := db.Query("SELECT * FROM items where category=?", category)

	if err != nil {
		fmt.Println("Err", err.Error())
		return nil
	}

	if results.Next() {
		err = results.Scan(&prod.Id, &prod.Name, &prod.Category, &prod.Location, &prod.Time, &prod.ImageName, &prod.ImageWidth, &prod.ImageHeight, &prod.Phone, &prod.OwnerAccount, &prod.Attributes, &prod.Description, &prod.Comments)
		if err != nil {
			return nil
		}
	} else {

		return nil
	}

	return prod
}

func AddItem(item Item) int64 {
	insert, err := db.Exec(
		"INSERT INTO items (name,category,location,time,imageName,imageWidth,imageHeight,phone,ownerAccount,attributes,description,comments) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)",
		item.Name, item.Category, item.Location, item.Time, item.ImageName, item.ImageWidth, item.ImageHeight, item.Phone, item.OwnerAccount, item.Attributes, item.Description, item.Comments)

	// if there is an error inserting, handle it
	if err != nil {
		panic(any(err.Error()))
	}
	id, err := insert.LastInsertId()
	if err != nil {

	}
	return id
}

func DeleteItem(id int) (*Item, error) {
	prod := &Item{}
	results, err := db.Query("SELECT * FROM items where id=?", id)

	for results.Next() {
		err = results.Scan(&prod.Id, &prod.Name, &prod.Category, &prod.Location, &prod.Time, &prod.ImageName, &prod.ImageWidth, &prod.ImageHeight, &prod.Phone, &prod.OwnerAccount, &prod.Attributes, &prod.Description, &prod.Comments)
		if err != nil {
			return prod, err
		}
	}

	_, err = db.Query("DELETE FROM items where id=?", id)
	if err != nil {
		fmt.Println("Err", err.Error())
		return prod, err
	}

	return prod, nil
}

func Close() {
	err := db.Close()
	if err != nil {
		fmt.Println("Err", err.Error())
	}
}
