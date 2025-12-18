# ValidForm_DSL

This DSL is used to generate interactive form applications with:
  - data validation
  - input security
  - language localization
<p>in a declarative manner.</p>


## How to use this DSL
1. you need to add the `antlr-4.13.2-complete.jar` file to the java library
2. if you already add it but still get an error, then you can just add the `.jar` file\
   when you compile it (i will show you later on)
3. after that you can change how your input gonna be, like the format in `pendaftaran.dsl` file\
   you can change the field the formulir name and the rules within the fields
4. if you already done all the step above, lets compile it

## Compiling DSL
- **step 1**\
Compile `.g4` file first using `antlr`

> `java -jar antlr-4.13.2-complete.jar ValidForm.g4`

- **step 2**\
After you ompile the .g4 file, it will create parser lexer listener java file,\
so compile all .java file

> `javac -cp ".;antlr-4.13.2-complete.jar" *.java`

- **step 3**\
Then run ValidFormCOmpiler and pendaftaran.dsl
> `java -cp ".;antlr-4.13.2-complete.jar" ValidFormCompiler pendaftaran.dsl`

- **step 4**\
Running the ValidFromCompiler will create the app, so compile\
the generated file

> `javac AplikasiPendaftaran.java`

- **step 5**\
Run the file and you can see the error messages if you\
violate the rules the you already made

> `java AplikasiPendaftaran`

<br>

>[!WARNING]
>This is a project from subject Programming Languange Pragmatics.\
>All we did in here was just learning and hope to be better
