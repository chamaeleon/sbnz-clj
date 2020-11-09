# sbnz

A simple SBNZ (Subtract, Branch If Not Zero) VM Interpreter.

## Usage

    $ lein run program.sbnz
    $ lein run program.sbnz memory.txt

## Options

Takes one or two arguments. First argument is a SBNZ source code file, the optional second argument is a filename that will contain a memory dump of the VM memory image in the form of space separated integers.

## Examples

Source code stored in a file named program.sbnz

    SBNZ [0] [0] a 4
    SBNZ [0] [7] b 8
    SBNZ [0] [-9] c 12
    SBNZ c a a 16
    SBNZ [1] b b 1024
    SBNZ [0] [0] [0] 12
    OUTPUT a
    OUTPUT b
    OUTPUT c

Run SBNZ program, whose purpose is to calculate 7 (stored in variable `b`) times 9 (stored in variable `c`) and store the result in variable `a`. The result of this computation is 63 as expected. The output shows that `b`'s original value of 7 has been decremented to 0 in a loop that has executed 7 times, and that `c` is initialized to -9 so that `a - c` results in `a - (-9)`, or equivalently`a + 9`.

    $ lein run program.sbnz
    63
    0
    -9

The memory space for the VM is 0-1023. Using a jump address less than 0 or greater than 1023 (like 1024 in the program above) will be interpreted as end of program execution

The SBNZ instructions are composed of 4 memory references which can be of the following types

Reference type | Description
<number> | A direct memory cell reference (0, 4, 1024, etc.)
<variable name> | A reference to a memory cell whose address will be computed (a, b, c, foo, bar, etc.)
[<number>] | An integer that will be stored in a memory cell whose address will be computed, ([0], [1], [7], etc.)

The OUTPUT directive takes one argument that can be one of the following types

    <number>: A direct memory cell reference
    <variable name>: A reference to a memory cell whose address will be computed

## License

Copyright 2020 Lars Nilsson

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.