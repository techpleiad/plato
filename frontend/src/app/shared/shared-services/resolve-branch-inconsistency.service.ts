import { HttpClient } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ResolveBranchInconsistencyService {

  private BASE: any;
  constructor(private http: HttpClient, @Inject('API_END_POINT') private API_END_POINT: any) {
    this.BASE = API_END_POINT.PLATO;
  }
  sendMergeRequest(body: any){
    return this.http.post(`${this.BASE.ADD_SERVICES.URL}/resolve-inconsistency`, body);
  }

}
