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
  displayedColumns = ['position', 'property', 'services', 'branches', 'profiles'];


  constructor(private _rulesDataService: RulesDataService) {}

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
          console.log(this.dataSource);
        });
  }
  
  

}
