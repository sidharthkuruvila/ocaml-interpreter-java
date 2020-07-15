{
  type token =
  | Number of int
  | Operation of string
  | Open_bracket
  | Close_bracket

  let repr token = match token with
  | Number n -> "[Number(" ^ (string_of_int n) ^ ")]"
  | Operation o -> "[Operation(" ^ o ^ ")]"
  | Open_bracket -> "[(]"
  | Close_bracket -> "[)]"
}

let number = ['0'-'9']+
let operation = ['+' '/' '*' '-']
let open_bracket = '('
let close_bracket = ')'
let space = [ ' ' '\t' '\n' ]+

rule token = parse
  | space { token lexbuf }
  | number { Number (int_of_string (Lexing.lexeme lexbuf))  }
  | operation { Operation (Lexing.lexeme lexbuf) }
  | open_bracket { Open_bracket }
  | close_bracket { Close_bracket }

{

}
