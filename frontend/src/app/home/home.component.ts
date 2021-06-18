import { Component, OnInit } from '@angular/core';
import { microService } from '../microService';
import { DataManagerService } from '../shared/shared-services/data-manager.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  title = 'frontend';
  services: microService[] = [];
  servicesAll: microService[] = [];

  constructor(private _dataManagerService: DataManagerService) { }

  ngOnInit(): void {
    this._dataManagerService.getServicesList()
        .subscribe(data => {
          this.servicesAll = JSON.parse(JSON.stringify(data));
          this.services = this.servicesAll;
        });
  }
  reloadDisplayServices(){
    this._dataManagerService.getServicesList()
        .subscribe(data => {
          this.services = JSON.parse(JSON.stringify(data));
          
        });
  }

  //custom-manager, Custom-Manager, CUSTOM-MANAGER
  lowerize(input: string) {  
    let words = input.split('-');  
    let temp: string = words[0].toLowerCase();
    for(let i=1;i<words.length;i++){
      temp = temp.concat('-',words[i].toLowerCase());
    } 
    return temp;
  }
  filterDisplayServices(searchText: string){
    // Manipulating the search text
    let searchTextParts = searchText.split(" ");
    let manipulatedSearchText: string = searchTextParts[0];
    for(let i=1;i<searchTextParts.length;i++){
      manipulatedSearchText = manipulatedSearchText.concat("-",searchTextParts[i]);
    }
    //console.log(manipulatedSearchText);
    let searchLength = searchText.length;
    let filteredServices: microService[] = [];
    for(let i=0;i<this.servicesAll.length;i++){
      if( (this.servicesAll[i].service.slice(0,searchLength) === manipulatedSearchText)|| 
      (this.servicesAll[i].service.slice(0,searchLength) === this.lowerize(manipulatedSearchText))){
        filteredServices.push(this.servicesAll[i]);
      }
    }
    this.services = filteredServices;
  }

}
