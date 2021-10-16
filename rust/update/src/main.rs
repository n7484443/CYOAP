use fs_extra::dir::CopyOptions;
use std::env;
extern crate fs_extra;

use std::fmt::Result;
use fs_extra::dir::move_dir;
use fs_extra::dir::remove;

fn main(){
    println!("update start");
    
    copy();
    println!("update end");
}

fn copy() -> Result{
    let dir =  env::current_dir().expect("error");
    let path = dir.parent().expect("error 2");
    let path_origin = path.join("newImage").join("image");
    let options = CopyOptions::new();
    if path_origin.exists(){
        println!("update exist");
        let path_origin_mother = path.join("newImage");
        let path_to = path.join("image");
        remove(&path_to).expect("error with removing");
        move_dir(&path_origin, &path, &options).expect("error with moving");
        remove(&path_origin_mother).expect("error with removing after move");
    }
    Ok(())
}