var express = require('express');
var router = express.Router();

const mongoose=require('mongoose')
let Signs=mongoose.model("signs")


router.post('/count',function(req,res) {
	Signs.find(req.body).countDocuments().exec(function(err,sum) {
		res.json({"num":sum})
	})
});

router.post('/upload',function(req,res) {
	Signs.create(req.body,function(err) {
		res.json({"info":"ok"})
	})
});

router.post('/getdata',function(req,res) {
	Signs.find(req.body).sort({'_id':-1}).exec(function(err,ava) {
			res.json(ava)
	})
});

module.exports = router;
