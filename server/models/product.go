package models

import (
	"database/sql"
	"fmt"
	_ "github.com/go-sql-driver/mysql"
)

type Item struct {
	Id           int    `json:"id"`
	Name         string `json:"name"`
	Category     string `json:"category"`
	Location     string `json:"Location"`
	Time         string `json:"time"`
	ImagePath    string `json:"image_path"`
	ImageWidth   int    `json:"image_width"`
	ImageHeight  int    `json:"image_height"`
	Phone        string `json:"phone"`
	OwnerAccount string `json:"owner_account"`
	Attributes   string `json:"attributes"`
	Description  string `json:"description"`
}

/*
CREATE TABLE items(
    `id` int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(255) NOT NULL,
    `category` VARCHAR(255) NOT NULL,
    `location` VARCHAR(255) NOT NULL,
    `time` VARCHAR(255) NOT NULL,
    `imagePath` VARCHAR(255) NOT NULL,
    `imageWidth` INT NOT NULL,
    `imageHeight` INT NOT NULL,
    `phone` VARCHAR(255) NOT NULL,
    `ownerAccount` VARCHAR(255) NOT NULL,
    `attributes` VARCHAR(255) NOT NULL,
    `description` VARCHAR(255) NOT NULL
) ;
*/

func GetItems() []Item {
	dbuser := "root"
	dbpass := "200430"
	dbname := "mutualhelpers"
	db, err := sql.Open("mysql", dbuser+":"+dbpass+"@tcp(127.0.0.1:3306)/"+dbname)
	// if there is an error opening the connection, handle it
	if err != nil {
		// simply print the error to the console
		fmt.Println("Err", err.Error())
		// returns nil on error
		return nil
	}
	defer db.Close()
	results, err := db.Query("SELECT * FROM items")
	if err != nil {
		fmt.Println("Err", err.Error())
		return nil
	}

	var items []Item
	for results.Next() {
		var prod Item
		// for each row, scan into the Product struct
		err = results.Scan(&prod.Id, &prod.Name, &prod.Category, &prod.Location, &prod.Time, &prod.ImagePath, &prod.ImageWidth, &prod.ImageHeight, &prod.Phone, &prod.OwnerAccount, &prod.Attributes, &prod.Description)
		if err != nil {
			panic(any(err.Error())) // proper error handling instead of panic in your app
		}
		// append the product into products array
		items = append(items, prod)
	}

	return items

}

func GetItem(category string) *Item {
	dbuser := "root"
	dbpass := "200430"
	dbname := "mutualhelpers"
	db, err := sql.Open("mysql", dbuser+":"+dbpass+"@tcp(127.0.0.1:3306)/"+dbname)
	prod := &Item{}
	if err != nil {
		// simply print the error to the console
		fmt.Println("Err", err.Error())
		// returns nil on error
		return nil
	}

	defer db.Close()

	results, err := db.Query("SELECT * FROM items where category=?", category)

	if err != nil {
		fmt.Println("Err", err.Error())
		return nil
	}

	if results.Next() {
		err = results.Scan(&prod.Id, &prod.Name, &prod.Category, &prod.Location, &prod.Time, &prod.ImagePath, &prod.ImageWidth, &prod.ImageHeight, &prod.Phone, &prod.OwnerAccount, &prod.Attributes, &prod.Description)
		if err != nil {
			return nil
		}
	} else {

		return nil
	}

	return prod
}

func AddItem(item Item) {
	dbuser := "root"
	dbpass := "200430"
	dbname := "mutualhelpers"
	db, err := sql.Open("mysql", dbuser+":"+dbpass+"@tcp(127.0.0.1:3306)/"+dbname)

	if err != nil {
		panic(any(err.Error()))
	}

	// defer the close till after this function has finished
	// executing
	defer db.Close()

	insert, err := db.Query(
		"INSERT INTO items (name,category,location,time,imagePath,imageWidth,imageHeight,phone,ownerAccount,attributes,description) VALUES (?,?,?,?,?,?,?,?,?,?,?)",
		item.Name, item.Category, item.Location, item.Time, item.ImagePath, item.ImageWidth, item.ImageHeight, item.Phone, item.OwnerAccount, item.Attributes, item.Description)

	// if there is an error inserting, handle it
	if err != nil {
		panic(any(err.Error()))
	}

	defer insert.Close()

}
