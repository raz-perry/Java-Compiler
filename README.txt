nitzan_karby
raz.perry


=============================
=      File description     =
=============================

Variable.java - This class represents a variable with its modifier, type and flag if already assigned.
AllRegex.java - This is a static class with regex expressions as static members

main package:

Sjavac.java - This class is the main driver that reads the file and use BodyCheck object to validate the code.
IllegalAmountOfParametersException.java - This is an exception class of wrong parameters amount in the
input arguments (print 2).
IllegalInputException.java - This is an exception class of errors while trying to read the file (print 2).

blockCheckers package:

ScopeCheck.java - This is an abstract class of different scopes in the code which checking validation
of Sjavac code. Each type of Scope need to inherit from it (full body, methods, if/while and even classes in
the future).
BodyCheck.java - This class inherited from BlockCheck and override the default isLegal implementation
(only include variables and methods).
MethodCheck.java - This class inherited from BlockCheck and use it default methods to validate the method code
lines. It also delegates ObjectCheck to use its beneficial functionality of valid row method.
ConditionCheck.java - This class inherited from BlockCheck and use it default methods to validate the
condition code (if/while blocks). It also delegates ObjectCheck to use its beneficial functionality of valid
row method.
ObjectCheck.java - This class expends the functionality of valid row - also find calling methods rows and
return statements as legal.
typeCheck.java - This is an enum class that define which types to check (char, int, String...) and how.
IllegalCodeException.java - This is an exception class of syntax errors in the given code (the catch print 1).

=============================
=          Design           =
=============================

We choose to create checker class for each scope that possible in the code - the all body, the methods and
also for if&while sections. All those classes inherited from abstract class called ScopeCheck.

We also create class of variable which holds modifier, type and has the functionality to update and check if
the variable already assigned.

Each scope holds it specific global and local variables and the exist methods. Also Each has the method
isLegal (which also implements in the abstract class) and the same basic checks on general rows
(empty/columns/variables) which implements in the validRow method in the abstract class. If Scope has
more functionality (for example return statement is legal in while scope but isn't legal in the full body) it
can override the method, adds the functionality and than called the super for using the basic checks (which
relevant for all). By that design if asked to add new scope all needed is to add new class and its new
functionality if exist.
The variable checks is depended in the row area (different check for regular definition, variables in method
signature and ect) so we decided to keep it in the scopeCheck and not to create another specific class check.

If and while scopes behave the same - signature including condition and block rows with basic options,
method calls, return statements and inner scopes. So we create the same class for both - ConditionCheck.

Delegation use:
If, while and method has the same extra functionality (which doesn't exist in the full body) so we create
another class called ObjectChecker which implements this functionality and each ScopeCheck delegate it.

One choice principle:
There is a specific definition for each variable type according to the exercise rules (int could be only
digits), so we defined all those checks in one place - enum called TypeCheck. By defining data member of name
and abstract method of getReg each new type has to define the name that should be in the code (case sensitive)
and regex of the valid values to assign. So if asked to add new types or to change their behavior, there is
no need to make any change in other classes (close for changes) and just need to update this enum class.
There is also casting method which gets a type (string) and checks if it is valid according to the instance
name (for assignments, method calls and ect). As default it compares with type name but each object can
override this method and define specific casting (for example casting from int to double).
Furthermore the regex of type is creating according to this enum (by the names)

In general we use many private methods to hide the implementation and make the main methods more readable.

=============================
=  Implementation details   =
=============================

Scopes implementation:
Each scope has three main data members (hashMap data structures):
1. known global variables
2. local variables
3. known methods
isLegal is the main method of each scope which manage all kinds of valid code lines that can be:

First of all it is looking for a new scopeCheck (according to the exercise rules only while/if).
If found new scopeCheck it skips over all inner scope rows (doesn't check their validation yet) and
when finish it creates the new object with the relevant rows. Than it calls the new object to check its
validation. In this case the body works a little differently - first it reads the all code and creates the
inner objects (methods) and just when finish reading all it is calling each method to check its validation.

When reaching a line that isn't relevant to a new scope it use the helper method validRow.
This method has default implementation in the abstract class with general row checks that relevant for all
scopeChecks possible objects: empty/comment rows, variable definition/assignment and ect.

Regex:
We defined a lot of data members that relevant to many different patterns (like end code line). when we needed
the same pattern for different matchers we initialized it before and just change the matcher with the new
text.

Exceptions:
We create general exception for type 1 errors (illegal code) to manage to errors in a single position - by
using the exceptions there is no tracing back and it just go back to the main driver - Sjavac.
We also create two different IOExceptions with informative error messages to print in case of type 2 errors.

=============================
=    Answers to questions   =
=============================
1. As we wrote in the design about the enum class:
To add new type like float, all needed is to add FLOAT to the enum with relevant name ("float") and override
the getReg method with return value of the relevant regex for those values (something like double regex) -
this method and name values are abstracts so the compiler will force us to add them.
That's all, there is no need to change anything in any other class.


2. Two new implementations:
* Using methods of standard java -
All needed is adding one line at the beginning of validRow method in ScopeCheck:
If java's statements can be a regex all needed is to add that regex there. Otherwise we should create a new
class with all data needed and a method that checks if a row is java's statement. And in validRow we just add
the one line of calling that method of that class.


* Different methods' types (int foo()) -
There are three things to do:
isLegal called "checkCall" method which check if the signature is valid. So the first change is in that method
in the bodyCheck - the current regex is for method with void modifier, so we need to add also type options
(types string regex already exist) and group it to save the type for later.
The next thing is adding to methodCheck object new data member of type and getter and setter for it. By that,
when creating the method in isLegal, we need to add a row of calling the setter with the found regex found in
change 1.
The last thing is to change the return regex in ObjectCheck, specificValidRow method by adding the regex that
relevant to the specific method type (this functionality exist by using the enum).


3. Two regex expressions:
1)
regex used in are code is VAR_NAME = "\\b_[a-zA-Z0-9_]+\\b|\\b[a-zA-Z][a-zA-Z0-9_]*\\b"
we will go over it by part. The regex is build of two options separated by a pipe. Both statements are bounded
by a "\\b" command declaring that other than the single demanded parameter, no other parameter other than
spaces can show in the pattern. the main difference between the two options is that a var name can start with
a "_" and than there has to be at least one more valid characters or with letters and than it can be only one
character. Both statements are greedy formed.

2)
each enum instance has data member of name ("int", "double"...), so we create the legal type regex by that
method:
private static String typeRegMaker (){
        String var = "";
        for (TypeCheck type: TypeCheck.values()){
            var += type.name;
            var += "|";
        }
    return var.substring(0, var.length()-1);
}
by the pipe we get the or option. we called it to static data member of type regex and any time we use it we
add parenthesis before and after (because the pipe).





