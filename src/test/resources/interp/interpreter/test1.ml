
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
  print_endline "test_weak";
  let default = option_or_else ~default:"default" in
  print_endline "test_weak";
  let a = Weak.create 3 in
  Weak.set a 1 (Some "abc");
  Weak.set a 2 None;
  print_endline (default (Weak.get a 0));
  print_endline (default (Weak.get a 1));
  print_endline (default (Weak.get a 2))

let test_mutual_recursion () =
  print_endline "test_mutual_recursion";
  let rec is_odd x =
    if x = 0 then false else is_even (x - 1)
  and is_even x =
    if x = 0 then true else is_odd (x - 1) in
  print_endline (string_of_bool (is_odd 10));
  print_endline (string_of_bool (is_odd 9))

let test_lexing () =
  print_endline "test_lexing";
  let lexbuf = Lexing.from_string "2*(3+4)\n" in
  try
    while true do
      let t = Test_lex.token lexbuf in
      print_endline (Test_lex.repr t)
    done
  with Failure msg ->
    print_endline ("Final message:" ^ msg)


let print_backtrace_slot slot =
  let open Printexc in
  let info pos is_raise =
    if is_raise then
      if pos = 0 then "Raised at" else "Re-raised at"
    else
      if pos = 0 then "Raised by primitive operation at" else "Called from" in
  let is_raise = Printexc.Slot.is_raise slot in
  let loc = Option.get (Printexc.Slot.location slot) in
  let filename = loc.filename in
  let line_number = loc.line_number in
  let start_char = loc.start_char in
  let end_char = loc.end_char in
  let is_inline = Slot.is_inline slot in
  print_endline (Printf.sprintf "%s %s in file \"%s\"%s, line %d, characters %d-%d"
    (info 0 is_raise) "Stdlib.failwith" filename (if is_inline then " (inlined)" else "") line_number start_char end_char);
  print_endline ""

let test_backtrace () =
 print_endline "test_backtrace";
 Printexc.record_backtrace true;
 try
   failwith "Some failure"
 with Failure msg -> begin
   let raw_backtrace = Printexc.get_raw_backtrace () in
   let raw_slot = Printexc.get_raw_backtrace_slot raw_backtrace 0 in
   let slot = Printexc.convert_raw_backtrace_slot raw_slot in
   print_endline (match Printexc.Slot.format 0 slot with
   | Some s -> "Some(" ^ s ^ ")"
   | None -> "None");
   print_endline (match Printexc.Slot.format 1 slot with
      | Some s -> "Some(" ^ s ^ ")"
      | None -> "None");
   print_endline "xxxxx";
   print_backtrace_slot slot;
   print_endline "yyyyy";
   Printexc.print_backtrace stdout
end

let test_int () =
  print_endline "test_int";
  let a = 23 lsl 2 in
  print_endline (string_of_int a)


let test_printf () =
  print_endline "test_printf";
  let info pos is_raise =
    if is_raise then
      if pos = 0 then "Raised at" else "Re-raised at"
    else
      if pos = 0 then "Raised by primitive operation at" else "Called from" in
  print_endline (Printf.sprintf "%s %s in file \"%s\"%s, line %d, characters %d-%d"
    (info 0 true) "Stdlib.failwith" "stdlib.ml" "" 29 17 33);
  Printf.printf "%s %s in file \"%s\"%s, line %d, characters %d-%d"
    (info 0 true) "Stdlib.failwith" "stdlib.ml" "" 29 17 33;
  print_endline ""

let test_buffer () =
  print_endline "test_buffer";
  let buf = Buffer.create 64 in
  Buffer.add_char buf 'a';
  Buffer.add_char buf 'b';
  Buffer.add_string buf "cat";
  Buffer.add_string buf "dog";
  Buffer.add_char buf 'b';
  Buffer.add_uint8 buf 75;
  let s = Buffer.contents buf in
  print_endline ("x" ^ s ^ "x")


let test_bytes () =
  print_endline "test_bytes";
  let s = Bytes.of_string "abc" in
  Bytes.unsafe_set s 0 'X';
  print_endline (Bytes.to_string s)

(*hello world*)
let _ = begin
  test_bytes ();
  test_buffer ();
  test_printf ();
  test_int ();
  test_backtrace ();
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
