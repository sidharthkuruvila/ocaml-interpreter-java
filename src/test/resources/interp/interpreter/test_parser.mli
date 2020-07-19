
(* The type of tokens. *)

type token = 
  | PLUS
  | OPEN_PAREN
  | NUM of (int)
  | MUL
  | MINUS
  | EOF
  | DIV
  | CLOSE_PAREN

(* This exception is raised by the monolithic API functions. *)

exception Error

(* The monolithic API. *)

val prog: (Lexing.lexbuf -> token) -> Lexing.lexbuf -> (int option)
