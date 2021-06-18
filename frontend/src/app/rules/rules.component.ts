import { Component, OnInit } from '@angular/core';
import { rulesTemplate } from '../rulesTemplate';
import { RulesDataService } from '../shared/shared-services/rules-data.service';


@Component({
  selector: 'app-rules',
  templateUrl: './rules.component.html',
  styleUrls: ['./rules.component.css']
})
export class RulesComponent implements OnInit {

  rulesList: rulesTemplate[] = [];
  constructor(private _rulesDataService: RulesDataService) {}

  ngOnInit(): void {
    this._rulesDataService.getRulesList()
        .subscribe(data => {
          this.rulesList = JSON.parse(JSON.stringify(data));
          console.log(this.rulesList);
        });
  }

}
