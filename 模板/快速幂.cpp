#include<iostream>
using namespace std;
using ll = long long;

ll fastPow(ll a,ll b, ll c){
    ll res =1;
    while(b>0){
        if(b&1)res = res*a%c;
        b>>=1;
        a=a*a%c;
    }
    return res;
}

int main(){
    int a,b,c;
    a=2;
    b=9;
    c = 1e+7;
    cout<<fastPow(a,b,c)<<"\n";
}