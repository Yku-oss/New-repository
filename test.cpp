#include <windows.h>
#include <iostream>;
using namespace std;
int main(){
    for(int i=1;i<=100;i++){
        system("gen.exe");
        system("brute.exe");
        system("std.exe");
        if(system("fc 1.out 1.ans")){
            cout<<"WA! on test "<<i<<"\n";
            return 0;
        }else{
            cout<<"AC on test "<<i<<"\n";
        }
    }
}