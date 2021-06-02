import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { microService } from 'src/app/microService';
import * as yaml from 'yaml';

@Injectable({
  providedIn: 'root'
})
export class DataManagerService {

  private _base_url = "http://localhost:8080/v1/services/";

  constructor(private http: HttpClient) { }
  getServicesList(){
    return this.http.get(this._base_url);
  }
  yamlToJson(temp: string){
    return yaml.parse(temp);
  }


  addService(newService: microService): Observable<any>{
    let serviceItems = JSON.stringify(newService);
    //console.log(serviceItems);
    return this.http.post(this._base_url, newService);

  }
}

