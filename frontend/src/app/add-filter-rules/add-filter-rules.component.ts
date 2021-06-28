import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { CustomValidateDialogueComponent } from '../custom-validate-dialogue/custom-validate-dialogue.component';
import { JsonSchemaWriterComponent } from '../json-schema-writer/json-schema-writer.component';

@Component({
  selector: 'app-add-filter-rules',
  templateUrl: './add-filter-rules.component.html',
  styleUrls: ['./add-filter-rules.component.css']
})
export class AddFilterRulesComponent implements OnInit {
  @Output() filter_rules = new EventEmitter();
  searchProperty: string = "";
  searchServices: string = "";

  constructor(public dialog: MatDialog) { }

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
  openCustomValidationDialog(){
    const dialogRef = this.dialog.open(CustomValidateDialogueComponent,{
      width: "600px",
      minHeight: "300px"
    });

    dialogRef.afterClosed().subscribe((result: any)=>{
      if(result){
        console.log(result);
      }
      else{
        console.log("null value");
      }
    });
  }
  

}
