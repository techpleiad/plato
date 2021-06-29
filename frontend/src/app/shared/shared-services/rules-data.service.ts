import { HttpClient } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { addRuleTemplate } from 'src/app/addRuleTemplate';

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
    return this.http.post(this.BASE.ADD_RULES.URL, addNewRule);
  }
}
