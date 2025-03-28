import {Det3dView, DParams, DetParams} from "./vdetails.js";

console.log("hi from main")

//let v1 = Det3dView.create3dView("canvas3d_1")

//let v2 = Det3dView.create3dView("canvas3d_2")

//let v3 = Det3dView.create3dView("canvas3d_3")

let d = DParams.fromJSON(' {"dtype":"ductR", "a":400, "b":300 }')
let g = new DetParams()


console.log(d)
console.log(g)

DParams.tt()


let j = d.toJSON()

console.log(j)
