using DigitalDeivice.Models;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using System.Text;

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
// Cấu hình Authentication với JWT
builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
	.AddJwtBearer(options =>
	{
		options.TokenValidationParameters = new TokenValidationParameters
		{
			ValidateIssuer = true,
			ValidateAudience = true,
			ValidateLifetime = true,
			ValidateIssuerSigningKey = true,
			ValidIssuer = "duccdung@gmail.com",
			ValidAudience = "duccdung@gmail.com",
			IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes("sjbvsdbvcskjbcvskjvcbsdkjcvbsdkjcvbsdkjcbsdkjc"))
		};
	});

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
