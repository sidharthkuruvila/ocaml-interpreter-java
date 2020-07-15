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


let test_nativeint _ =
  print_endline "Test nativeint";
  let open Nativeint in
  let a = of_float 3.5 in
  let b = of_int 4 in
  let c = of_string "7" in
  let d = match of_string_opt "abc" with | None -> of_int 23 | Some _ -> failwith "Expected None" in
  let e = one in
  let f = abs minus_one in
  let g = add b c in
  let h = of_int (compare b c) in
  let i = div c a in
  let j = match of_string_opt "17" with | None -> failwith "Expected a number" | Some n -> n  in
  let k = [| a; b; c; d; e; f; g; h; i; j |] in
  let l = Array.fold_left add zero k in
  print_endline  (to_string l)

let test_int64 _ =
  print_endline "Test int64";
  let open Int64 in
  let a = of_float 3.5 in
  let b = Int64.of_int 4 in
  let c = of_string "7" in
  let d = match of_string_opt "abc" with | None -> of_int 23 | Some _ -> failwith "Expected None" in
  let e = one in
  let f = abs minus_one in
  let g = add b c in
  let h = of_int (compare b c) in
  let i = div c a in
  let j = match of_string_opt "17" with | None -> failwith "Expected a number" | Some n -> n  in
  let k = [| a; b; c; d; e; f; g; h; i; j |] in
  let l = Array.fold_left add zero k in
  print_endline  (to_string l)

let test_ref _ =
  let x = ref 1 in
  x := 1 + !x;
  print_endline (string_of_int !x)

exception Exp of string

let throws_exception _ =
  raise (Exp "Exception")

let test_exception _ = begin
  print_endline "test_exception";
  try
    throws_exception ()
  with Exp message ->
    print_endline message
end


class foo =
object(self)
  val mutable v = "hello"
  method get = v
  method set x = v <- x
end

let test_oops _ =
  print_endline "test_oops";
  let o = new foo in
  let a: string = Obj.obj (Obj.field (Obj.repr o) 2) in
  print_endline a;
  o#set "world";
  print_endline (o#get)

let test_sys _ =
  print_endline "test_sys";
  let env_path = Sys.getenv "PATH" in
  print_endline env_path;
  let env_o = Sys.getenv_opt "XYZ_UNKNOWN" in
  print_endline "boo";
  let cur_dir = Sys.getcwd () in
  print_endline cur_dir;
  Sys.chdir "..";
  print_endline (Sys.getcwd ());
  Sys.chdir cur_dir;
  print_endline (Sys.getcwd ());
  print_endline (make_string_from_array ~f:(fun x -> x) ~sep:", " (Sys.argv));
  print_endline (Bool.to_string (Sys.is_directory cur_dir));
  let files = Sys.readdir "." in
  print_endline (make_string_from_array ~f:(fun x -> x) ~sep:", " (files))

let test_hash () =
  print_endline "test_hash";
  let open Hashtbl in
  let a = hash 123 in
  let b = hash "abc" in
  let c = hash [| 1.; 2.; 3.|] in
  let d = hash [| "a"; "b"; "c" |] in
  let e = hash (new foo) in
  let x = a + b + c + d + e in
  print_endline (string_of_int x)

let option_or_else ~default v =
  match v with
  | Some v -> v
  | None -> default

let test_weak () =
  let default = option_or_else ~default:"default" in
  print_endline "test_weak";
  let a = Weak.create 3 in
  Weak.set a 1 (Some "abc");
  Weak.set a 2 None;
  print_endline (default (Weak.get a 0));
  print_endline (default (Weak.get a 1));
  print_endline (default (Weak.get a 2))

let test_mutual_recursion () =
  let rec is_odd x =
    if x = 0 then false else is_even (x - 1)
  and is_even x =
    if x = 0 then true else is_odd (x - 1) in
  print_endline (string_of_bool (is_odd 10));
  print_endline (string_of_bool (is_odd 9))



let test_lexing () =
  let lexbuf = Lexing.from_string "2*(3+4)\n" in
  try
    while true do
      let t = Test_lex.token lexbuf in
      print_endline (Test_lex.repr t)
    done
  with Failure msg ->
    print_endline ("Final message:" ^ msg)


(*hello world*)
let _ = begin
  test_lexing ();
  test_mutual_recursion ();
  test_weak ();
  test_hash ();
  test_int64 ();
  test_nativeint ();
  test_sys ();
  test_oops ();
  test_exception ();
  test_ref ();
  test1 ();
  test2 ();
  test3 ();
  test4 ();
  test5 ();
  test6 ();
  test7 ();
  test8 ();
  test9 ();
  ()
end
