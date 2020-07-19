%token <int> NUM
%token PLUS
%token MINUS
%token MUL
%token DIV
%token OPEN_PAREN
%token CLOSE_PAREN
%token EOF

%left PLUS MINUS        /* lowest precedence */
%left MUL DIV         /* medium precedence */

%start <int option> prog
%%

prog:
  | EOF       { None }
  | v = expr EOF { Some v }
  ;

expr:
  | i = NUM { i }
  | OPEN_PAREN e = expr CLOSE_PAREN { e }
  | e1 = expr PLUS e2 = expr { e1 + e2 }
  | e1 = expr MINUS e2 = expr { e1 - e2 }
  | e1 = expr MUL e2 = expr { e1 * e2 }
  | e1 = expr DIV e2 = expr { e1 / e2 }
  ;
