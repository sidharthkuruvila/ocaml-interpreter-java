{
  open Test_parser

  let repr token = match token with
    | NUM n -> "[Number(" ^ (string_of_int n) ^ ")]"
    | PLUS -> "[+]"
    | MINUS -> "[-]"
    | MUL -> "[*]"
    | DIV -> "[/]"
    | OPEN_PAREN -> "[(]"
    | CLOSE_PAREN -> "[)]"
    | EOF -> "[;]"

  exception Error of string
}

let number = ['0'-'9']+
let plus = '+'
let div = '/'
let mul = '*'
let minus = '-'
let open_bracket = '('
let close_bracket = ')'
let space = [ ' ' '\t' '\n' ]+

rule token = parse
  | space { token lexbuf }
  | number { NUM (int_of_string (Lexing.lexeme lexbuf))  }
  | plus { PLUS }
  | minus { MINUS }
  | mul { MUL }
  | div { DIV }
  | open_bracket { OPEN_PAREN }
  | close_bracket { CLOSE_PAREN }
  | eof { EOF }
{

}
