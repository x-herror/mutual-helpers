package main

import (
	"fmt"
	"github.com/gin-gonic/gin"
	"net/http"
	"os"
	"path"
	"server/models"
)

func main() {
	router := gin.Default()
	router.GET("/items", getItems)
	router.GET("/images/:imageName", getImage)
	router.GET("/items/:category", getItem)
	router.POST("/items", addItem)
	router.POST("/images", addImage)
	//router.DELETE()
	//router.PUT()
	router.Run()
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

func getImage(c *gin.Context) {
	//https://www.jianshu.com/p/6950550b2937
	//fileDir := c.Query("fileDir")
	//fileName := c.Query("fileName")
	imageName := c.Param("imageName")
	//打开文件
	_, errByOpenFile := os.Open(path.Join("./images", imageName))
	if errByOpenFile != nil {
		c.AbortWithStatus(http.StatusNotFound)
	}
	c.Header("Content-Type", "application/octet-stream")
	c.Header("Content-Disposition", "attachment; filename="+fileName)
	c.Header("Content-Transfer-Encoding", "binary")
	c.File(fileDir + "/" + fileName)
	return
}

func addImage(c *gin.Context) {
	form, err := c.MultipartForm()
	if err != nil {
		c.AbortWithStatus(http.StatusBadRequest)
	}
	files := form.File["image"]
	//https://blog.csdn.net/lvjie13450/article/details/123164878
	for _, file := range files {
		dest := path.Join("./images/", file.Filename)
		err := c.SaveUploadedFile(file, dest)
		if err != nil {
			fmt.Println("Err", err.Error())
		}
	}
}
