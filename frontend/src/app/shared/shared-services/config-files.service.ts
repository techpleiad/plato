import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import * as yaml from 'yaml';

@Injectable({
  providedIn: 'root'
})
export class ConfigFilesService {

  private _base_url = "http://localhost:8080/v1/services/";

  constructor(private http: HttpClient) { }
  getFile(service:any, type:any, branch:any, profile:any){
    let modified_url = this._base_url.concat(service.toString(),"/branches/",branch.toString(),"?format=yaml&type=",type.toString(),"&profile=",profile.toString());
    //console.log("modified url is");
    console.log(modified_url);
    return this.http.get(modified_url,{responseType: 'text'});
  }

  

  yamlToJson(temp: string){
    return yaml.parse(temp);
  }
}
