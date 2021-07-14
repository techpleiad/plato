import { HttpClient } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { addRuleTemplate } from 'src/app/addRuleTemplate';
import { customValidate } from 'src/app/customValidate';

@Injectable({
  providedIn: 'root'
})
export class RulesDataService {

  private BASE: any;
  constructor(private http: HttpClient, @Inject('API_END_POINT') private API_END_POINT: any) {
    this.BASE = API_END_POINT.PLATO; 
  }
  getRulesList(){
    return this.http.get(this.BASE.GET_RULES.URL); 
  }

  addRule(addNewRule: addRuleTemplate): Observable<any>{
    console.log(JSON.stringify(addNewRule,null,2));
    console.log(addNewRule);

    return this.http.post(this.BASE.ADD_RULES.URL, addNewRule);
  }

  sendCustomValidateEmail(cusVal: customValidate){
    console.log(cusVal);
    return this.http.post(`${this.BASE.ADD_SERVICES.URL}/branches/custom-validate`, cusVal);
  }
}
