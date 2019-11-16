var express = require('express');
var router = express.Router();

const mongoose=require('mongoose')
let Students=mongoose.model("students")


router.post('/ifexist',function(req,res){
	Students.findOne(req.body,function(err,info) {
		 res.json(info)
	})
});
router.post('/upload',function(req,res) {
	Students.create(req.body,function(err) {
		res.json({"info":"ok"})
	})
});

router.post('/getdata',function(req,res) {
	Students.findOne(req.body,function(err,ava) {
			res.json(ava)
	})
});

module.exports = router;
