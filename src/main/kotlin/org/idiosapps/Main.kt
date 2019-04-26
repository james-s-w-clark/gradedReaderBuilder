package org.idiosapps

// https://stackoverflow.com/a/34833438/4261132
// To make a fat jar with JavaFX,
//    we can't have the MainClass extend Application
// So, unfortunately we have to hack 2 static methods to get to the app!
class Main {
    companion object{
        @JvmStatic
        fun main(args: Array<String>) {
            MainApp.main(args)
        }
    }
}