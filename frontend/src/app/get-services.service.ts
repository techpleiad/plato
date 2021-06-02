import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as yaml from 'yaml';
//import * as jsonDiff from 'json-diff';
//import * as diff from 'deep-diff';

@Injectable({
  providedIn: 'root'
})
export class GetServicesService {
  private _BASEurl_getServices = "http://localhost:8080/v1/services/";

  constructor(private http: HttpClient) { }
  getServicesList(){
    return this.http.get(this._BASEurl_getServices);
  }
  yamlToJson(temp: string){
    return yaml.parse(temp);
  }
}
