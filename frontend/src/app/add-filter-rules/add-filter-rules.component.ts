import { Component, EventEmitter, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-add-filter-rules',
  templateUrl: './add-filter-rules.component.html',
  styleUrls: ['./add-filter-rules.component.css']
})
export class AddFilterRulesComponent implements OnInit {
  @Output() filter_rules = new EventEmitter();
  searchProperty: string = "";
  searchServices: string = "";

  constructor() { }

  ngOnInit(): void {
  }
  filterProperty(event: any){
    this.searchProperty = event.target.value;
    this.filter_rules.emit({property:this.searchProperty,services:this.searchServices});
  }
  filterServices(event: any){
    this.searchServices = event.target.value;
    this.filter_rules.emit({property:this.searchProperty,services:this.searchServices});
  }

}
