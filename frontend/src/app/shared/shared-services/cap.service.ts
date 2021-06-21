import { HttpClient } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class CapService {

  private BASE: any;
  constructor(private http: HttpClient, @Inject('API_END_POINT') private API_END_POINT: any) {
    this.BASE = API_END_POINT.PLATO;
  }
  getReport(branch: any, tempObject: any){
    return this.http.post<any[]>(`${this.BASE.GET_SERVICES.URL}/branches/${branch}/across-profiles-validate`,tempObject);
  }
   
}
