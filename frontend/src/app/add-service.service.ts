import { Injectable } from '@angular/core';
import { microService } from './microService';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as yaml from 'yaml';
@Injectable({
  providedIn: 'root'
})
export class AddServiceService {

  constructor(private http: HttpClient) {
    
  }

  addNewService(newService: microService): Observable<any>{
    let serviceItems = JSON.stringify(newService);
    console.log(serviceItems);
    let putUrl="localhost:8080/v1/services";
    return this.http.post(putUrl, serviceItems);

  }
}
