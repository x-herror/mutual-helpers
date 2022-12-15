package badger

//https://github.com/dgraph-io/badger
import (
	"fmt"
	badger "github.com/dgraph-io/badger/v3"
	"log"
)

var kvdb *badger.DB

func init() {
	var err error
	kvdb, err = badger.Open(badger.DefaultOptions("./"))
	if err != nil {
		log.Fatal(err)
	}

}

func Close() {
	err := kvdb.Close()
	if err != nil {

	}
}

func Add() {
	err := kvdb.Update(func(txn *badger.Txn) error {
		err := txn.Set([]byte("answer"), []byte("42"))
		return err
	})
	if err != nil {

	}
}

func Get() {
	err := kvdb.View(func(txn *badger.Txn) error {
		item, err := txn.Get([]byte("answer"))
		if err != nil {
			fmt.Println("Err", err.Error())
		}
		var valCopy []byte
		// Alternatively, you could also use item.ValueCopy().
		valCopy, err = item.ValueCopy(nil)
		fmt.Printf("The answer is: %s\n", valCopy)
		return err
	})
	if err != nil {
		fmt.Println("Err", err.Error())
	}
}

func Delete() {
	err := kvdb.Update(func(txn *badger.Txn) error {
		err := txn.Delete([]byte("answer"))
		if err != nil {
			fmt.Println("Err", err.Error())
		}
		return err
	})
	if err != nil {
		fmt.Println("Err", err.Error())
	}
}
