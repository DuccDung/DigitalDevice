using Microsoft.EntityFrameworkCore;
using DigitalDeivice.Models;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.

builder.Services.AddControllers();
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();


builder.Services.AddDbContext<DigitalDeviceContext>(options =>
    options.UseSqlServer(builder.Configuration.GetConnectionString("DigitalDeviceContext")));

// Lắng nghe trên IP cụ thể
builder.WebHost.UseUrls("http://192.168.40.131:5168", "https://192.168.40.131:7012");


var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();

app.UseAuthorization();

app.MapControllers();
// Cho phép truy cập các tệp tĩnh từ thư mục wwwroot
app.UseStaticFiles();
app.MapControllers();

app.Run();
