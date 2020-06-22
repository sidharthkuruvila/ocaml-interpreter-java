
let make_string_from_array ~f ~sep array =
  let e = ref (f (Array.get array 0)) in
  for i = 1 to (Array.length array) - 1 do
    e := !e ^ sep ^ (f (Array.get array i))
  done;
  !e

let test1 _ = print_endline "Hello World"
let test2 _ =
  let a = 1 in
  let b = 2 in
  let c = a + b in
  print_endline (string_of_int c)

let test3 _ =
  let l = [1;2;3;4;5;6;7;8;9;10] in
  let lsq = List.map (fun a -> a * a) l in
  let n = List.fold_left (fun a b -> a + b) 0 lsq in
  print_endline (string_of_int n)

let test4 _ =
  let a = 1.5 in
  let b = 2.5 in
  let c = a +. b in
  print_endline (string_of_float c)

let test5 _ =
  let arr = [|1.;2.;3.;4.;5.;6.;7.;8.;9.;10.|] in
  let asq = Array.map (fun a -> a *. a) arr in
  let n = Array.fold_left (fun a b -> a +. b) 0. asq in
  print_endline (string_of_float n)

let test6 _ =
  let a1 = [|1.;2.;3.|] in
  let a2 = [|4.;5.;6.|] in
  let arr = Array.append a1 a2 in
  let str = make_string_from_array ~f:string_of_float ~sep:", " arr in
  print_endline str

let test7 _ = begin
  let a1 = [|1.;2.;3.|] in
  let a2 = [|4.;5.;6.|] in
  let a3 = Array.make 3 7. in
  let a4 = Array.make 3 8. in
  let arr = Array.concat [a1; a2; a3; a4] in
  let arr2 = Array.make 8 9. in
  let arr3 = Array.create_float 8 in
  Array.blit arr 1 arr2 2 4;
  Array.blit arr 1 arr3 2 4;
  let str = make_string_from_array ~f:string_of_float ~sep:", " arr in
  print_endline str;
  let str = make_string_from_array ~f:string_of_float ~sep:", " arr2 in
  print_endline str;
  let str = make_string_from_array ~f:string_of_float ~sep:", " arr3 in
  print_endline str;
  let arr5 = Array.sub arr 2 4 in
  let str = make_string_from_array ~f:string_of_float ~sep:", " arr5 in
  print_endline str
end
let test8 _ = begin
  let a1 = [| 4 |] in
  let n1 = Array.get a1 0 in
  Array.set a1 0 (n1 + 1);
  print_endline (make_string_from_array ~f:string_of_int ~sep:", " a1);
  let a2 = [| 4. |] in
  let n2 = Array.get a2 0 in
  Array.set a2 0 (n2 +. 1.);
  print_endline (make_string_from_array ~f:string_of_float ~sep:", " a2)
end

let test9 _ =
  let a = 0.5 in
  let b = Float.asin 1. in
  let c = Float.sin 1. in
  let d = Float.cos 1. in
  let e = Float.acos 1. in
  let f = Float.tan 1. in
  let g = Float.atan f in
  let h = Float.atan2 f f in
  let i = Float.abs (Float.neg (1.)) in
  let s = Array.fold_left (fun a b -> a +. b) 0. [| a;b;c;d;e;f;g;h;i|] in
  print_endline (string_of_float s);
  print_endline (string_of_float (Float.ceil s));
  print_endline (string_of_float (Float.floor s))

(*hello world*)
let _ = begin
  test1 ();
  test2 ();
  test3 ();
  test4 ();
  test5 ();
  test6 ();
  test7 ();
  test8 ();
  test9 ()
end
