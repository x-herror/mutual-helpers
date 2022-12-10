package main

import (
	"github.com/gin-gonic/gin"
	"net/http"
	"server/models"
)

func main() {
	router := gin.Default()
	router.GET("/items", getItems)
	router.GET("/items/:category", getItem)
	router.POST("/items", addItem)
	router.Run("localhost:8083")
}

func getItems(c *gin.Context) {
	products := models.GetItems()

	if products == nil || len(products) == 0 {
		c.AbortWithStatus(http.StatusNotFound)
	} else {
		c.IndentedJSON(http.StatusOK, products)
	}
}

func getItem(c *gin.Context) {
	category := c.Param("category")

	item := models.GetItem(category)

	if item == nil {
		c.AbortWithStatus(http.StatusNotFound)
	} else {
		c.IndentedJSON(http.StatusOK, item)
	}
}

func addItem(c *gin.Context) {
	var prod models.Item

	if err := c.BindJSON(&prod); err != nil {
		c.AbortWithStatus(http.StatusBadRequest)
	} else {
		models.AddItem(prod)
		c.IndentedJSON(http.StatusCreated, prod)
	}
}
