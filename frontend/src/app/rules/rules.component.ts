import { Component, OnInit } from '@angular/core';
import { rulesTemplate } from '../rulesTemplate';
import { RulesDataService } from '../shared/shared-services/rules-data.service';


export class RuleDisplay {
  position: number=0;
  property: string;
  services: string[];
  branches: string[];
  profiles: string[];
  constructor(position:number, property:string, services:string[], branches:string[], profiles:string[]){
    this.position = position;
    this.property = property;
    this.services = services;
    this.branches = branches;
    this.profiles = profiles;
  }
}

@Component({
  selector: 'app-rules',
  templateUrl: './rules.component.html',
  styleUrls: ['./rules.component.css']
})
export class RulesComponent implements OnInit {

  rulesList: rulesTemplate[] = [];
  dataSource: RuleDisplay[] = [];
  dataSourceAll: RuleDisplay[] = [];
  displayedColumns = ['position', 'property', 'services', 'branches', 'profiles'];


  constructor(private _rulesDataService: RulesDataService) {}

  // If branches/profiles empty --> include all the branches/profiles.
  ngOnInit(): void {
    this._rulesDataService.getRulesList()
        .subscribe(data => {
          this.rulesList = JSON.parse(JSON.stringify(data));
          console.log(this.rulesList);
          let position:number = 0;
          this.dataSource = this.rulesList.map((val)=>{
            position = position+1;
            return new RuleDisplay(position,val.ruleOnProperty,val.scope.services,val.scope.branches,val.scope.profiles);
          })
          this.dataSourceAll = this.dataSource;
          console.log(this.dataSource);
        });
  }
  clickedRow(rule: any){
    console.log(rule);
    console.log(this.rulesList[rule.position-1])
    //accessing the rule from the position and send it to other window. 
    //---> send(ruleList[position-1]).
  }
  filterRules(event: any){
    this.dataSource = this.dataSourceAll;
    let searchProperty = event.property.toLowerCase();
    let searchServices = event.services.toLowerCase();
    console.log(searchProperty);
    console.log(searchServices);
    let filteredProperty:RuleDisplay[] = [];
    if(searchProperty===""){
      filteredProperty = this.dataSource;
    }
    else{
      for(let i=0;i<this.dataSource.length;i++){
        if(this.dataSource[i].property.includes(searchProperty)){
          filteredProperty.push(this.dataSource[i]);
        }
      }
    }
    let filteredServices: RuleDisplay[] = [];
    if(searchServices===""){
      filteredServices = filteredProperty;
    }
    else{
      for(let i=0;i<filteredProperty.length;i++){
        for(let j=0;j<filteredProperty[i].services.length;j++){
          if(filteredProperty[i].services[j].includes(searchServices)){
            filteredServices.push(filteredProperty[i]);
          }
        }
      }
    }
    
    this.dataSource = filteredServices;

  }

}
