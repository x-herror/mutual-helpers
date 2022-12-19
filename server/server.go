package main

import (
	"fmt"
	"github.com/gin-gonic/gin"
	"log"
	"net/http"
	"os"
	"path"
	"server/badger"
	"server/models"
	"strconv"
	"time"
)

const FORMAT = "2006.01.02-15:04:05"

var timeStamp int64 = 0

func main() {

	router := gin.Default()
	//router.GET("/images/:imageName", getImage)
	router.GET("/update", handleUpdate)
	router.GET("/items/:category", getItem)
	router.GET("/items", getItems)

	router.Static("/images", "./images")
	router.Static("/avatars", "./avatars")

	router.POST("/items", addItem)
	router.POST("/images", addImage)
	router.POST("/avatars", addAvatar)

	router.DELETE("/items", deleteItem)

	/*
		router.GET("/categories",getCategories)
		router.POST("/categories",addCategory)
		router.DELETE("/categories",deleteCategory)

		router.GET("/persons",getPersons)
		router.POST("/persons",addPerson)


		router.GET("/waitpersons",getWaitPersons)
		router.POST("/waitpersons",addWaitPerson)
		router.DELETE("/waitpersons",deleteWaitPerson)

	*/

	router.Run()
	models.Close()
}

func handleUpdate(c *gin.Context) {
	t, _ := strconv.Atoi(c.Query("timeStamp"))
	t64 := int64(t)
	if timeStamp > t64 {
		//TODO
	} else {
		c.JSON(http.StatusOK, gin.H{"update": "not"})
	}
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
		setTimeStamp(prod.Time)
	}
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

func addAvatar(c *gin.Context) {
	form, err := c.MultipartForm()
	if err != nil {
		c.AbortWithStatus(http.StatusBadRequest)
	}
	files := form.File["avatar"]
	for _, file := range files {
		dest := path.Join("./avatars/", file.Filename)
		err := c.SaveUploadedFile(file, dest)
		if err != nil {
			fmt.Println("Err", err.Error())
		}
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
	c.Header("Content-Type", "image/png")
	//https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Disposition
	c.Header("Content-Disposition", "attachment; filename="+imageName)
	c.Header("Content-Transfer-Encoding", "binary")
	c.File("./images" + imageName)
	return
}

func deleteItem(c *gin.Context) {
	rawId := c.Query("id")
	id, err := strconv.Atoi(rawId)
	if err != nil {
		c.AbortWithStatus(http.StatusNotFound)
	}
	_, err = models.DeleteItem(id)
	if err != nil {
		fmt.Println("Err", err.Error())
	}
	setTimeStamp(time.Now().Format(FORMAT))
	//TODO:item.ImagePath
}

func addCategory(c *gin.Context) {
	badger.Add()
}

func setTimeStamp(t string) {
	loc, _ := time.LoadLocation("Asia/Shanghai")
	tt, _ := time.ParseInLocation(FORMAT, t, loc)
	timeStamp = tt.Unix()
	log.Println("update time:", timeStamp)
}
