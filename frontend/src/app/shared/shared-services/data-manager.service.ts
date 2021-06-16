import { HttpClient } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { branchConsistency } from 'src/app/branchConsistency';
import { microService } from 'src/app/microService';
import { profileConsistency } from 'src/app/profileConsistency';
import * as yaml from 'yaml';


@Injectable({
  providedIn: 'root'
})
export class DataManagerService {


  private BASE: any;
  constructor(private http: HttpClient, @Inject('API_END_POINT') private API_END_POINT: any) {
    this.BASE = API_END_POINT.PLATO; 
   }
  getServicesList(){
    return this.http.get(this.BASE.GET_SERVICES.URL); ////http://localhost:8080/vi/services

  }
  yamlToJson(temp: string){
    return yaml.parse(temp);
  }


  addService(newService: microService): Observable<any>{
    console.log(newService);
    return this.http.post(this.BASE.ADD_SERVICES.URL, newService);
  }

  sendBranchConsistencyEmail(branchCons: branchConsistency): Observable<any>{
    console.log(branchCons);
    return this.http.post(`${this.BASE.ADD_SERVICES.URL}/branches/across-branches-validate`, branchCons);
  }

  sendProfileConsistencyEmail(profileCons: profileConsistency, branchValue: string): Observable<any>{
    console.log(profileCons);
    return this.http.post(`${this.BASE.ADD_SERVICES.URL}/branches/${branchValue}/across-profiles-validate`, profileCons);
  }
}

