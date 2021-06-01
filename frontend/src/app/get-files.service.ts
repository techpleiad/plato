import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as yaml from 'yaml';
//import * as jsonDiff from 'json-diff';
//import * as diff from 'deep-diff';

@Injectable({
  providedIn: 'root'
})
export class GetFilesService {

  private _BASEurl_getServices = "http://localhost:8080/v1/services/";

  constructor(private http: HttpClient) { }
  getFile(service:any, branch:any, profile:any){
    let modified_url = this._BASEurl_getServices.concat(service.toString(),"/branches/",branch.toString(),"?format=yaml&type=merged&profile=",profile.toString());
    //console.log("modified url is");
    console.log(modified_url);
    return this.http.get(modified_url,{responseType: 'text'});
  }

  

  yamlToJson(temp: string){
    return yaml.parse(temp);
  }
}
