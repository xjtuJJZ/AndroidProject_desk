const mongoose=require('mongoose')
const modelstu=require('./models/stu/')
const modelsign=require('./models/sign/')
let Students=mongoose.model("students",modelstu.studentSchema)
let Signs=mongoose.model("signs",modelsign.signSchema)

