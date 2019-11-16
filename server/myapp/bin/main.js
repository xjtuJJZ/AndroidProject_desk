const mongoose=require('mongoose')
const mongo=require('../mongo')
const app=require('../app')
const http=require('http')

let mongoUrl='mongodb://localhost:27017/jjz'
let httpPort=3000
let server=http.createServer(app)
mongoose.connect(mongoUrl,{useNewUrlParser:true,useUnifiedTopology:true},err=> {
	server.listen(httpPort)
})
server.on('listening',()=> {
	console.log("express start!")
})
server.on('error',(err)=>console.log("express error"))
	mongoose.connection.on('open',function() {
})

mongoose.connection.on('open',function() {
	console.log("mongoose connected!")
})

mongoose.connection.on('error',function(err) {
	console.log("mongoose connection error")
})


